package com.example.demo.dto.user;

public class UserDashboardDTO {

    private long favorites;
    private long playlists;
    private String listeningTime;
    private long recentlyPlayed;

    public UserDashboardDTO(long favorites,
                            long playlists,
                            String listeningTime,
                            long recentlyPlayed) {
        this.favorites = favorites;
        this.playlists = playlists;
        this.listeningTime = listeningTime;
        this.recentlyPlayed = recentlyPlayed;
    }

    public long getFavorites() { return favorites; }
    public long getPlaylists() { return playlists; }
    public String getListeningTime() { return listeningTime; }
    public long getRecentlyPlayed() { return recentlyPlayed; }
}