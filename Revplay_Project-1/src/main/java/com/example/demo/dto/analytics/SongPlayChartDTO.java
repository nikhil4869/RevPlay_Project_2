package com.example.demo.dto.analytics;

public class SongPlayChartDTO {

    private String title;
    private long plays;

    public SongPlayChartDTO(String title, long plays) {
        this.title = title;
        this.plays = plays;
    }

    public String getTitle() { return title; }
    public long getPlays() { return plays; }
}