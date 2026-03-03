package com.example.demo.dto.analytics;

import java.util.List;

public class ArtistAnalyticsDTO {

    private long totalSongs;
    private long totalAlbums;
    private long totalPlays;
    private long totalFavorites;
    private List<String> topSongs;

    public long getTotalSongs() { return totalSongs; }
    public void setTotalSongs(long totalSongs) { this.totalSongs = totalSongs; }

    public long getTotalAlbums() { return totalAlbums; }
    public void setTotalAlbums(long totalAlbums) { this.totalAlbums = totalAlbums; }

    public long getTotalPlays() { return totalPlays; }
    public void setTotalPlays(long totalPlays) { this.totalPlays = totalPlays; }

    public long getTotalFavorites() { return totalFavorites; }
    public void setTotalFavorites(long totalFavorites) { this.totalFavorites = totalFavorites; }

    public List<String> getTopSongs() { return topSongs; }
    public void setTopSongs(List<String> topSongs) { this.topSongs = topSongs; }
}