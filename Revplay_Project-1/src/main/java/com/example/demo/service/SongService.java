package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.analytics.SongFavoriteStatsDTO;
import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface SongService {

	SongDTO uploadSong(String title,
            String genre,
            String duration,
            MultipartFile audioFile,
            Long albumId,
            Integer trackNumber,
            Integer releaseYear);

    
	SongDTO uploadCover(Long songId, MultipartFile image);


	List<SongDTO> getMySongs();
	
	void addSongToAlbum(Long songId, Long albumId, Integer trackNumber);

	List<SongDTO> getAlbumSongs(Long albumId);
	
	void removeFromAlbum(Long songId);

	void reorderTrack(Long songId, Integer newTrackNumber);

	void deleteSong(Long songId);
	
	List<SongDTO> getAllSongs();

	SongDTO getSongDetails(Long songId);
	
	List<SongDTO> getPublicSongs();

	List<SongFavoriteStatsDTO> getFavoriteStatsForMySongs();

}
