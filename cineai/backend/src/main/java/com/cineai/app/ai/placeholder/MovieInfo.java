package com.cineai.app.ai.placeholder;

import com.cineai.app.ai.dto.ShowtimeDto;
import java.util.List;

public class MovieInfo {
    private final String id;
    private final String title;
    private final String posterUrl;
    private final double rating;
    private final int durationMinutes;
    private final List<String> genres;      // từ GENRES table
    private final List<ShowtimeDto> showtimes; // từ SHOWTIMES + CINEMAS

    public MovieInfo(String id, String title, String posterUrl, double rating,
                     int durationMinutes, List<String> genres, List<ShowtimeDto> showtimes) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.durationMinutes = durationMinutes;
        this.genres = genres;
        this.showtimes = showtimes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public double getRating() { return rating; }
    public int getDurationMinutes() { return durationMinutes; }
    public List<String> getGenres() { return genres; }
    public List<ShowtimeDto> getShowtimes() { return showtimes; }
}
