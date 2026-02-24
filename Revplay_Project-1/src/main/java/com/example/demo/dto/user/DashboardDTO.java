package com.example.demo.dto.user;



import com.example.demo.dto.music.SongDTO;
import java.util.List;

public class DashboardDTO {

    private long totalSongsLiked;
    private long totalPlaylists;
    private long totalSongsPlayed;
    private long totalListeningMinutes;

    private List<SongDTO> recentlyPlayed;
    private List<SongDTO> mostPlayed;
    
	public long getTotalSongsLiked() {
		return totalSongsLiked;
	}
	public void setTotalSongsLiked(long totalSongsLiked) {
		this.totalSongsLiked = totalSongsLiked;
	}
	public long getTotalPlaylists() {
		return totalPlaylists;
	}
	public void setTotalPlaylists(long totalPlaylists) {
		this.totalPlaylists = totalPlaylists;
	}
	public long getTotalSongsPlayed() {
		return totalSongsPlayed;
	}
	public void setTotalSongsPlayed(long totalSongsPlayed) {
		this.totalSongsPlayed = totalSongsPlayed;
	}
	public long getTotalListeningMinutes() {
		return totalListeningMinutes;
	}
	public void setTotalListeningMinutes(long totalListeningMinutes) {
		this.totalListeningMinutes = totalListeningMinutes;
	}
	public List<SongDTO> getRecentlyPlayed() {
		return recentlyPlayed;
	}
	public void setRecentlyPlayed(List<SongDTO> recentlyPlayed) {
		this.recentlyPlayed = recentlyPlayed;
	}
	public List<SongDTO> getMostPlayed() {
		return mostPlayed;
	}
	public void setMostPlayed(List<SongDTO> mostPlayed) {
		this.mostPlayed = mostPlayed;
	}

   
}
