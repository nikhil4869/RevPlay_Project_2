package com.example.demo.entity;

<<<<<<< HEAD
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
=======
import jakarta.persistence.*;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

@Entity
@Table(name = "SONG")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String genre;
<<<<<<< HEAD
=======
    
    private Integer releaseYear;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

    private String duration;

    @Column(nullable = false)
    private String audioPath;   // stored file path

    private String coverImage;  // optional album art

<<<<<<< HEAD
    @Column(nullable = false)
    private boolean isPublic = true;

    //  NEW FIELD - track popularity
    @Column(nullable = false)
    private Long playCount = 0L;

    // Analytics ready fields
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

=======
    private boolean isPublic = true;

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    // song belongs to an artist
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;
<<<<<<< HEAD

=======
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    private Integer trackNumber;

<<<<<<< HEAD
    public Song() {}

    // Automatically set timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

=======
    @Column(nullable = false)
    private Long playCount = 0L;

    public Song() {}

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
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

<<<<<<< HEAD
    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

=======
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    public User getArtist() {
        return artist;
    }

    public void setArtist(User artist) {
        this.artist = artist;
    }
<<<<<<< HEAD

=======
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
<<<<<<< HEAD

=======
    
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }
<<<<<<< HEAD
}
=======

    public long getPlayCount() { return playCount; }
    public void setPlayCount(long playCount) { this.playCount = playCount; }

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

}
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
