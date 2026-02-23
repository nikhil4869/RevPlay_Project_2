package com.example.demo.dto.user;

public class UserStatsDTO {

    private long totalSongsUploaded;
    private long totalSongsLiked;
    private long totalAlbums;

    public UserStatsDTO(long totalSongsUploaded,
                        long totalSongsLiked,
                        long totalAlbums) {
        this.totalSongsUploaded = totalSongsUploaded;
        this.totalSongsLiked = totalSongsLiked;
        this.totalAlbums = totalAlbums;
    }

    public long getTotalSongsUploaded() { return totalSongsUploaded; }
    public long getTotalSongsLiked() { return totalSongsLiked; }
    public long getTotalAlbums() { return totalAlbums; }
}