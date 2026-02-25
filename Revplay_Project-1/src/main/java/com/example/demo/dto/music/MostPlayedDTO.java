package com.example.demo.dto.music;

public class MostPlayedDTO {

    private Long songId;
    private String title;
    private long plays;

    public MostPlayedDTO(Long songId, String title, long plays) {
        this.songId = songId;
        this.title = title;
        this.plays = plays;
    }

    public Long getSongId() { return songId; }
    public String getTitle() { return title; }
    public long getPlays() { return plays; }
}