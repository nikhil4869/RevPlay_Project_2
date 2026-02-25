package com.example.demo.dto.analytics;

import java.util.List;

public class ArtistAnalyticsDTO {

    private long totalSongs;
    private long totalPlays;
    private long totalFavorites;
    private List<String> topSongs;

    public ArtistAnalyticsDTO(long totalSongs,
                              long totalPlays,
                              long totalFavorites,
                              List<String> topSongs) {
        this.totalSongs = totalSongs;
        this.totalPlays = totalPlays;
        this.totalFavorites = totalFavorites;
        this.topSongs = topSongs;
    }

    public long getTotalSongs() { return totalSongs; }
    public long getTotalPlays() { return totalPlays; }
    public long getTotalFavorites() { return totalFavorites; }
    public List<String> getTopSongs() { return topSongs; }
}