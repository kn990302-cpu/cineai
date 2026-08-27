package com.cineai.app.ai.dto;

import java.util.List;

/**
 * Field đặt tên khớp với tài liệu "Đặc tả bàn giao - Chatbot UI" (mục 4)
 * đã gửi cho Đức, để Đức bind thẳng vào Compose không cần đổi tên field.
 */
public class MovieRecommendationDto {

    private String movieTitle;
    private String posterUrl;
    private double rating;
    private int durationMinutes;
    private String reason;
    private List<ShowtimeDto> showtimes;

    public MovieRecommendationDto() {
    }

    public MovieRecommendationDto(String movieTitle, String posterUrl, double rating,
                                   int durationMinutes, String reason, List<ShowtimeDto> showtimes) {
        this.movieTitle = movieTitle;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.durationMinutes = durationMinutes;
        this.reason = reason;
        this.showtimes = showtimes;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ShowtimeDto> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<ShowtimeDto> showtimes) {
        this.showtimes = showtimes;
    }
}
