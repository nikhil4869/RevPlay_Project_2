package com.example.demo.dto.analytics;

import java.time.LocalDate;

public class DailyTrendDTO {

    private LocalDate date;
    private long plays;

    public DailyTrendDTO(LocalDate date, long plays) {
        this.date = date;
        this.plays = plays;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getPlays() {
        return plays;
    }
}