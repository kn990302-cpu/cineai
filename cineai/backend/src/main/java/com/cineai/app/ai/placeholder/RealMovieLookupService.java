package com.cineai.app.ai.placeholder;

import com.cineai.app.ai.dto.ShowtimeDto;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation THẬT - lấy dữ liệu từ Oracle DB của Thuận (schema AI_MOVIE).
 *
 * @Primary đảm bảo Spring dùng class này thay vì FakeMovieLookupService.
 * Khi Thuận đã chạy được DB, thêm @Primary vào đây và xoá @Service ở
 * FakeMovieLookupService là xong.
 *
 * Tên bảng/cột khớp đúng file AI_MOVIE.sql của Thuận:
 *   MOVIES, SHOWTIMES, GENRES, MOVIE_GENRES, CINEMA_ROOMS, CINEMAS
 */
@Primary
@Service
public class RealMovieLookupService implements MovieLookupService {

    private final JdbcTemplate jdbc;

    public RealMovieLookupService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<MovieInfo> findAllNowShowing() {
        // Lấy tất cả phim đang chiếu kèm danh sách thể loại (nối qua MOVIE_GENRES + GENRES)
        String sql = """
                SELECT
                    m.MOVIE_ID,
                    m.TITLE,
                    m.POSTER,
                    m.DURATION,
                    m.RATING,
                    LISTAGG(g.NAME, ',') WITHIN GROUP (ORDER BY g.NAME) AS GENRES
                FROM AI_MOVIE.MOVIES m
                LEFT JOIN AI_MOVIE.MOVIE_GENRES mg ON m.MOVIE_ID = mg.MOVIE_ID
                LEFT JOIN AI_MOVIE.GENRES g ON mg.GENRE_ID = g.GENRE_ID
                WHERE m.STATUS = 'NOW_SHOWING'
                GROUP BY m.MOVIE_ID, m.TITLE, m.POSTER, m.DURATION, m.RATING
                """;

        return jdbc.query(sql, (rs, rowNum) -> {
            String movieId = String.valueOf(rs.getLong("MOVIE_ID"));
            String genreStr = rs.getString("GENRES");
            List<String> genres = (genreStr != null && !genreStr.isBlank())
                    ? Arrays.asList(genreStr.split(","))
                    : Collections.emptyList();

            // Lấy giờ chiếu cho phim này
            List<ShowtimeDto> showtimes = findShowtimesForMovie(movieId);

            return new MovieInfo(
                    movieId,
                    rs.getString("TITLE"),
                    rs.getString("POSTER"),
                    rs.getDouble("RATING"),
                    rs.getInt("DURATION"),
                    genres,
                    showtimes
            );
        });
    }

    @Override
    public Optional<MovieInfo> findById(String movieId) {
        String sql = """
                SELECT
                    m.MOVIE_ID,
                    m.TITLE,
                    m.POSTER,
                    m.DURATION,
                    m.RATING,
                    LISTAGG(g.NAME, ',') WITHIN GROUP (ORDER BY g.NAME) AS GENRES
                FROM AI_MOVIE.MOVIES m
                LEFT JOIN AI_MOVIE.MOVIE_GENRES mg ON m.MOVIE_ID = mg.MOVIE_ID
                LEFT JOIN AI_MOVIE.GENRES g ON mg.GENRE_ID = g.GENRE_ID
                WHERE m.MOVIE_ID = ?
                GROUP BY m.MOVIE_ID, m.TITLE, m.POSTER, m.DURATION, m.RATING
                """;

        List<MovieInfo> results = jdbc.query(sql, (rs, rowNum) -> {
            String id = String.valueOf(rs.getLong("MOVIE_ID"));
            String genreStr = rs.getString("GENRES");
            List<String> genres = (genreStr != null && !genreStr.isBlank())
                    ? Arrays.asList(genreStr.split(","))
                    : Collections.emptyList();
            List<ShowtimeDto> showtimes = findShowtimesForMovie(id);

            return new MovieInfo(
                    id,
                    rs.getString("TITLE"),
                    rs.getString("POSTER"),
                    rs.getDouble("RATING"),
                    rs.getInt("DURATION"),
                    genres,
                    showtimes
            );
        }, Long.parseLong(movieId));

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // ─── Private: lấy giờ chiếu cho 1 phim ──────────────────────────────────
    // Join SHOWTIMES -> CINEMA_ROOMS -> CINEMAS để lấy tên rạp thật từ DB
    // thay vì hardcode "CGV Q.1" như prototype React trước đây.
    private List<ShowtimeDto> findShowtimesForMovie(String movieId) {
        String sql = """
                SELECT
                    s.SHOWTIME_ID,
                    TO_CHAR(s.START_TIME, 'HH24:MI') AS SHOW_TIME,
                    c.NAME AS CINEMA_NAME
                FROM AI_MOVIE.SHOWTIMES s
                JOIN AI_MOVIE.CINEMA_ROOMS cr ON s.ROOM_ID = cr.ROOM_ID
                JOIN AI_MOVIE.CINEMAS c ON cr.CINEMA_ID = c.CINEMA_ID
                WHERE s.MOVIE_ID = ?
                  AND s.START_TIME > CURRENT_TIMESTAMP
                ORDER BY s.START_TIME
                FETCH FIRST 5 ROWS ONLY
                """;

        return jdbc.query(sql, (rs, rowNum) -> new ShowtimeDto(
                String.valueOf(rs.getLong("SHOWTIME_ID")),
                rs.getString("SHOW_TIME"),
                rs.getString("CINEMA_NAME")
        ), Long.parseLong(movieId));
    }
}
