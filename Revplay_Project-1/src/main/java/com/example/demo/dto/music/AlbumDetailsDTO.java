package com.example.demo.dto.music;

import java.util.List;

public class AlbumDetailsDTO {

    private String albumName;
    private String artistName;
    private Integer releaseYear;
    private List<SongDTO> tracks;
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public Integer getReleaseYear() {
		return releaseYear;
	}
	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}
	public List<SongDTO> getTracks() {
		return tracks;
	}
	public void setTracks(List<SongDTO> tracks) {
		this.tracks = tracks;
	}

}
