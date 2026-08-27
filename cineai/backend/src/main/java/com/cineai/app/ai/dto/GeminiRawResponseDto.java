package com.cineai.app.ai.dto;

import java.util.List;

/**
 * Cấu trúc JSON mà mình YÊU CẦU Gemini trả về (khai báo rõ trong prompt).
 * Gemini KHÔNG biết poster/rating/duration thật trong DB, nên chỉ yêu cầu nó
 * trả movieId + reason -> AiService sẽ tự tra cứu dữ liệu thật từ DB để lấp đầy
 * các field còn lại (giống cách "ids" -> movies.find() ở bản prototype React).
 */
public class GeminiRawResponseDto {

    private String replyText;
    private List<Recommendation> recommendations;

    public static class Recommendation {
        private String movieId;
        private String reason;

        public String getMovieId() {
            return movieId;
        }

        public void setMovieId(String movieId) {
            this.movieId = movieId;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
