package com.cineai.app.ai.placeholder;

import java.util.List;
import java.util.Optional;

/**
 * !!! GHÉP VỚI THUẬN Ở ĐÂY !!!
 * Interface này là "hợp đồng" giữa AI Gateway và phần Database/Backend của Thuận.
 * - findAllNowShowing(): AiService dùng để liệt kê phim đang chiếu, đưa vào prompt cho Gemini.
 * - findById(): AiService dùng để lấy poster/rating/duration/showtimes THẬT sau khi
 *   Gemini đã chọn ra movieId phù hợp.
 *
 * Thuận chỉ cần viết 1 class implements MovieLookupService, lấy dữ liệu từ
 * MovieRepository/ShowtimeRepository thật của bạn ấy, rồi xoá FakeMovieLookupService đi.
 */
public interface MovieLookupService {

    List<MovieInfo> findAllNowShowing();

    Optional<MovieInfo> findById(String movieId);
}
