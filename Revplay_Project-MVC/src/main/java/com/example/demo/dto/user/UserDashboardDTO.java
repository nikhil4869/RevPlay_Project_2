package com.example.demo.dto.user;

public class UserDashboardDTO {
    private long favorites;
    private long playlists;
    private String listeningTime;
    private long recentlyPlayed;

    public UserDashboardDTO() {}

    public UserDashboardDTO(long favorites, long playlists, String listeningTime, long recentlyPlayed) {
        this.favorites = favorites;
        this.playlists = playlists;
        this.listeningTime = listeningTime;
        this.recentlyPlayed = recentlyPlayed;
    }

    public long getFavorites() { return favorites; }
    public void setFavorites(long favorites) { this.favorites = favorites; }
    public long getPlaylists() { return playlists; }
    public void setPlaylists(long playlists) { this.playlists = playlists; }
    public String getListeningTime() { return listeningTime; }
    public void setListeningTime(String listeningTime) { this.listeningTime = listeningTime; }
    public long getRecentlyPlayed() { return recentlyPlayed; }
    public void setRecentlyPlayed(long recentlyPlayed) { this.recentlyPlayed = recentlyPlayed; }
}
