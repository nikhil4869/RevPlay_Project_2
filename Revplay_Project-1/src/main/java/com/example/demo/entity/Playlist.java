package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private boolean isPublic;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistSong> songs;

    public Playlist() {}

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<PlaylistSong> getSongs() { return songs; }
    public void setSongs(List<PlaylistSong> songs) { this.songs = songs; }
}