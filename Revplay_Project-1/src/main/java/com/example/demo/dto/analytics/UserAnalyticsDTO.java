package com.example.demo.dto.analytics;

public class UserAnalyticsDTO {

    private long totalPlays;
    private long favoriteCount;
    private long playlistCount;
    private String mostPlayedSong;
    private String mostPlayedGenre;

    public long getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(long totalPlays) {
        this.totalPlays = totalPlays;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public long getPlaylistCount() {
        return playlistCount;
    }

    public void setPlaylistCount(long playlistCount) {
        this.playlistCount = playlistCount;
    }

    public String getMostPlayedSong() {
        return mostPlayedSong;
    }

    public void setMostPlayedSong(String mostPlayedSong) {
        this.mostPlayedSong = mostPlayedSong;
    }

    public String getMostPlayedGenre() {
        return mostPlayedGenre;
    }

    public void setMostPlayedGenre(String mostPlayedGenre) {
        this.mostPlayedGenre = mostPlayedGenre;
    }
}