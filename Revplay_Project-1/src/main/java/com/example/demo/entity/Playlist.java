package com.example.demo.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "playlist")
=======
import java.util.List;

@Entity
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
<<<<<<< HEAD

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isPublic = false;   // default private

    @ManyToOne
    @JoinColumn(name = "listener_id", nullable = false)
    private User listener;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    
    @OneToMany(mappedBy = "playlist",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PlaylistSong> songs;
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}