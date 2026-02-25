package com.example.demo.dto.playlist;

import com.example.demo.dto.music.SongDTO;
import java.util.List;

public class PlaylistDTO {

    private Long id;
    private String name;
<<<<<<< HEAD
    private boolean isPublic;
    private List<SongDTO> songs;
    private int totalSongs;

    public PlaylistDTO() {}

    public PlaylistDTO(Long id, String name, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public List<SongDTO> getSongs() {
        return songs;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setSongs(List<SongDTO> songs) {
        this.songs = songs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
=======
    private String description;
    private boolean isPublic;
    private String ownerName;

    public PlaylistDTO() {}

    public PlaylistDTO(Long id, String name, String description, boolean isPublic, String ownerName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.ownerName = ownerName;
    }
    

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isPublic() { return isPublic; }
    
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
    }
}