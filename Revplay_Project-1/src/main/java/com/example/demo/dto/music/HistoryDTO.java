package com.example.demo.dto.music;

import java.time.LocalDateTime;

public class HistoryDTO {

    private Long songId;
    private String title;
    private String artistName;
    private String audioUrl;
    private String songCover;
    private LocalDateTime playedAt;

    public HistoryDTO(Long songId, String title,
                      String artistName, String audioUrl, String songCover,
                      LocalDateTime playedAt) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.audioUrl = audioUrl;
        this.songCover = songCover;
        this.playedAt = playedAt;
    }

    public Long getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtistName() { return artistName; }
    public String getAudioUrl() { return audioUrl; }
    public String getSongCover() { return songCover; }
    public LocalDateTime getPlayedAt() { return playedAt; }
}