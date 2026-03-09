package com.example.demo.dto.music;

import java.time.LocalDateTime;

public class HistoryDTO {

    private Long songId;
    private String title;
    private String artistName;
    private String audioUrl;
    private String songCover;
    private LocalDateTime playedAt;

    public HistoryDTO() {}

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getSongCover() { return songCover; }
    public void setSongCover(String songCover) { this.songCover = songCover; }
    public LocalDateTime getPlayedAt() { return playedAt; }
    public void setPlayedAt(LocalDateTime playedAt) { this.playedAt = playedAt; }
}
