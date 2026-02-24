package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PLAYLIST_SONG")
public class PlaylistSong {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "PLAYLIST_ID", nullable = false)
	    private Playlist playlist;

	    @ManyToOne
	    @JoinColumn(name = "SONG_ID", nullable = false)
	    private Song song;

    public Long getId() {
        return id;
    }

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
    
//    public int getPosition() {
//        return position;
//    }
//
//    public void setPosition(int position) {   
//        this.position = position;
//    }
}