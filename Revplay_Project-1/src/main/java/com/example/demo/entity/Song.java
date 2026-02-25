package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SONG")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String genre;
    
    private Integer releaseYear;

    private String duration;

    @Column(nullable = false)
    private String audioPath;   // stored file path

    private String coverImage;  // optional album art

    private boolean isPublic = true;

    // song belongs to an artist
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;
    
    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private Integer trackNumber;

    @Column(nullable = false)
    private Long playCount = 0L;

    public Song() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public User getArtist() {
        return artist;
    }

    public void setArtist(User artist) {
        this.artist = artist;
    }
    
    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
    
    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public long getPlayCount() { return playCount; }
    public void setPlayCount(long playCount) { this.playCount = playCount; }

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

}
