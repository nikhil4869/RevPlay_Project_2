package com.example.demo.dto.analytics;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAnalyticsDTO {

    private long favoritesCount;
    private long playlistsCount;
    private String listeningTime;
    private List<Map<String, Object>> topSongs;
    private List<Map<String, Object>> topArtists;
    private List<Map<String, Object>> topGenres;

    public UserAnalyticsDTO() {}

    public UserAnalyticsDTO(long favoritesCount, long playlistsCount, String listeningTime, 
                            List<Map<String, Object>> topSongs, List<Map<String, Object>> topArtists, 
                            List<Map<String, Object>> topGenres) {
        this.favoritesCount = favoritesCount;
        this.playlistsCount = playlistsCount;
        this.listeningTime = listeningTime;
        this.topSongs = topSongs;
        this.topArtists = topArtists;
        this.topGenres = topGenres;
    }

    public long getFavoritesCount() { return favoritesCount; }
    public void setFavoritesCount(long favoritesCount) { this.favoritesCount = favoritesCount; }

    public long getPlaylistsCount() { return playlistsCount; }
    public void setPlaylistsCount(long playlistsCount) { this.playlistsCount = playlistsCount; }

    public String getListeningTime() { return listeningTime; }
    public void setListeningTime(String listeningTime) { this.listeningTime = listeningTime; }

    public List<Map<String, Object>> getTopSongs() { return topSongs; }
    public void setTopSongs(List<Map<String, Object>> topSongs) { this.topSongs = topSongs; }

    public List<Map<String, Object>> getTopArtists() { return topArtists; }
    public void setTopArtists(List<Map<String, Object>> topArtists) { this.topArtists = topArtists; }

    public List<Map<String, Object>> getTopGenres() { return topGenres; }
    public void setTopGenres(List<Map<String, Object>> topGenres) { this.topGenres = topGenres; }
}
