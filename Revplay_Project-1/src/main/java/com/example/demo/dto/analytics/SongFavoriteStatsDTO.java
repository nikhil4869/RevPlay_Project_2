package com.example.demo.dto.analytics;

public class SongFavoriteStatsDTO {

    private Long songId;
    private String title;
    private long favoriteCount;

    public SongFavoriteStatsDTO(Long songId, String title, long favoriteCount) {
        this.songId = songId;
        this.title = title;
        this.favoriteCount = favoriteCount;
    }

    public Long getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }
}
