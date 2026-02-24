package com.example.demo.dto.playlist;

import com.example.demo.dto.music.SongDTO;
import java.util.List;

public class PlaylistDTO {

    private Long id;
    private String name;
    private boolean isPublic;
    private List<SongDTO> songs;
    private int totalSongs;
    private int followerCount;

    public PlaylistDTO() {}

    public PlaylistDTO(Long id, String name, boolean isPublic,int followerCount) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.setFollowerCount(followerCount);
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
    }

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}
}