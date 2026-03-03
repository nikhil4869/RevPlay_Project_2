package com.example.demo.dto.analytics;

import java.time.LocalDate;

public class DailyTrendDTO {

    private LocalDate date;
    private long plays;

    public DailyTrendDTO() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getPlays() {
        return plays;
    }

    public void setPlays(long plays) {
        this.plays = plays;
    }
}