package com.cineai.app.ai.dto;

public class ShowtimeDto {

    private String id;
    private String time;
    private String cinema; // lấy từ CINEMAS.NAME trong DB của Thuận

    public ShowtimeDto() {}

    public ShowtimeDto(String id, String time, String cinema) {
        this.id = id;
        this.time = time;
        this.cinema = cinema;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getCinema() { return cinema; }
    public void setCinema(String cinema) { this.cinema = cinema; }
}
