package com.example.demo.dto.analytics;

public class SongPlayChartDTO {

    private String title;
    private long plays;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getPlays() { return plays; }
    public void setPlays(long plays) { this.plays = plays; }
}