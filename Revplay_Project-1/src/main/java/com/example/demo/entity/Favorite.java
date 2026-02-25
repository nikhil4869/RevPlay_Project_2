package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
<<<<<<< HEAD
@Table(name = "favorite")
=======
@Table(name = "FAVORITE",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"}))
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
<<<<<<< HEAD
    @JoinColumn(name = "listener_id", nullable = false)
    private User listener;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public Long getId() {
        return id;
    }

    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
=======
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    public Favorite() {}

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Song getSong() { return song; }
    public void setSong(Song song) { this.song = song; }
}
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
