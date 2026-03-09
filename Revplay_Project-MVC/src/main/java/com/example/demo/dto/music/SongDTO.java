package com.example.demo.dto.music;

public class SongDTO {

    private Long id;
    private String title;
    private String genre;
    private String duration;
    private String audioUrl;
    private String coverUrl;
    private String artistName;
    private String albumName;

    public SongDTO() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
    public String getAudioUrl() { return audioUrl; }
    public String getCoverUrl() { return coverUrl; }
    public String getArtistName() { return artistName; }
    public String getAlbumName() { return albumName; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public void setAlbumName(String albumName) { this.albumName = albumName; }
}