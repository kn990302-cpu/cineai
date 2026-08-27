package com.cineai.app.ai.dto;

import java.util.List;

/**
 * Response trả về cho POST /api/ai/chat khi thành công (trạng thái Success bên UI).
 */
public class ChatResponseDto {

    private String replyText;
    private List<MovieRecommendationDto> movies;

    public ChatResponseDto() {
    }

    public ChatResponseDto(String replyText, List<MovieRecommendationDto> movies) {
        this.replyText = replyText;
        this.movies = movies;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public List<MovieRecommendationDto> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieRecommendationDto> movies) {
        this.movies = movies;
    }
}
