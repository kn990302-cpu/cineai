package com.cineai.app.ai.service;

import com.cineai.app.ai.dto.ChatResponseDto;
import com.cineai.app.ai.dto.GeminiRawResponseDto;
import com.cineai.app.ai.dto.MovieRecommendationDto;
import com.cineai.app.ai.exception.AiGatewayException;
import com.cineai.app.ai.placeholder.MovieInfo;
import com.cineai.app.ai.placeholder.MovieLookupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AiService {

    private final GeminiClient geminiClient;
    private final MovieLookupService movieLookupService;
    private final ObjectMapper objectMapper;

    public AiService(GeminiClient geminiClient, MovieLookupService movieLookupService, ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.movieLookupService = movieLookupService;
        this.objectMapper = objectMapper;
    }

    public ChatResponseDto chat(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            throw new AiGatewayException("Câu hỏi trống, vui lòng nhập nội dung.");
        }

        List<MovieInfo> nowShowing = movieLookupService.findAllNowShowing();
        String prompt = buildPrompt(userMessage, nowShowing);

        String rawJson = geminiClient.generateJson(prompt);
        GeminiRawResponseDto raw = parseGeminiResponse(rawJson);

        List<MovieRecommendationDto> movies = mapToRealMovies(raw);

        if (movies.isEmpty()) {
            // Gemini không chọn được phim nào phù hợp - đây chính là nhánh
            // Error phía UI ("chưa tìm được phim phù hợp...")
            throw new AiGatewayException("Chưa tìm được phim phù hợp với yêu cầu này.");
        }

        return new ChatResponseDto(raw.getReplyText(), movies);
    }

    private String buildPrompt(String userMessage, List<MovieInfo> nowShowing) {
        StringBuilder movieListJson = new StringBuilder("[");
        for (MovieInfo m : nowShowing) {
            movieListJson.append("{\"id\":\"").append(m.getId())
                    .append("\",\"title\":\"").append(m.getTitle())
                    .append("\",\"durationMinutes\":").append(m.getDurationMinutes())
                    .append(",\"rating\":").append(m.getRating())
                    .append("},");
        }
        if (!nowShowing.isEmpty()) movieListJson.setLength(movieListJson.length() - 1);
        movieListJson.append("]");

        // Khai báo RÕ định dạng JSON mong muốn giúp Gemini bám sát cấu trúc hơn.
        return """
                Bạn là trợ lý gợi ý phim cho app đặt vé xem phim CineAI.
                Danh sách phim đang chiếu (JSON): %s

                Yêu cầu của người dùng: "%s"

                Hãy chọn tối đa 2 phim phù hợp nhất từ danh sách trên.
                Chỉ trả về JSON đúng định dạng sau, không thêm chữ nào khác:
                {
                  "replyText": "câu trả lời ngắn gọn, thân thiện bằng tiếng Việt",
                  "recommendations": [
                    { "movieId": "id lấy từ danh sách trên", "reason": "lý do phù hợp, ngắn gọn, có thể dùng 1 emoji" }
                  ]
                }
                Nếu không có phim nào phù hợp, trả về "recommendations": [].
                """.formatted(movieListJson, userMessage);
    }

    private GeminiRawResponseDto parseGeminiResponse(String rawJson) {
        try {
            return objectMapper.readValue(rawJson, GeminiRawResponseDto.class);
        } catch (Exception e) {
            throw new AiGatewayException("Trợ lý AI trả về định dạng không hợp lệ.", e);
        }
    }

    private List<MovieRecommendationDto> mapToRealMovies(GeminiRawResponseDto raw) {
        List<MovieRecommendationDto> result = new ArrayList<>();
        if (raw.getRecommendations() == null) return result;

        for (GeminiRawResponseDto.Recommendation rec : raw.getRecommendations()) {
            Optional<MovieInfo> movie = movieLookupService.findById(rec.getMovieId());
            movie.ifPresent(m -> result.add(new MovieRecommendationDto(
                    m.getTitle(), m.getPosterUrl(), m.getRating(),
                    m.getDurationMinutes(), rec.getReason(), m.getShowtimes()
            )));
            // Nếu Gemini "bịa" movieId không có thật trong DB, phim đó tự động
            // bị bỏ qua ở đây (Optional rỗng) - không hiển thị dữ liệu sai cho user.
        }
        return result;
    }
}
