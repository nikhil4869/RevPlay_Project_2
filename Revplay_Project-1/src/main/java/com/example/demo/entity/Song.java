package com.example.demo.entity;

<<<<<<< HEAD
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
=======
import jakarta.persistence.*;

@Entity
@Table(name = "SONG")
>>>>>>> origin/harish-dev
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    private String title;

    private Integer duration;

    public Song() {
    }

    public Song(String title, Integer duration) {
        this.title = title;
        this.duration = duration;
    }
=======
    @Column(nullable = false)
    private String title;

    private String genre;

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


    public Song() {}
>>>>>>> origin/harish-dev

    public Long getId() {
        return id;
    }

<<<<<<< HEAD
    public void setId(Long id) {
        this.id = id;
    }

=======
>>>>>>> origin/harish-dev
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

<<<<<<< HEAD
    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
=======
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


}
>>>>>>> origin/harish-dev
