package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
<<<<<<< HEAD
@Table(name = "playlist_song")
=======
@Table(name = "playlist_song",
       uniqueConstraints = @UniqueConstraint(columnNames = {"playlist_id","song_id"}))
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

=======
    // order of song inside playlist
    @Column(name = "track_order")
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public PlaylistSong() {}

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    public Long getId() {
        return id;
    }

<<<<<<< HEAD
=======
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}