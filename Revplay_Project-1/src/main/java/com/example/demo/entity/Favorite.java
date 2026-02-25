package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "FAVORITE",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
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
