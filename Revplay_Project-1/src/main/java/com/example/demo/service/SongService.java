package com.example.demo.service;

<<<<<<< HEAD
import java.util.List;

import com.example.demo.dto.music.SongDTO;

=======
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.music.SongDTO;
import java.util.List;

>>>>>>> origin/harish-dev
public interface SongService {
	
	List<SongDTO> getAllSongs();

	SongDTO uploadSong(String title,
            String genre,
            String duration,
            MultipartFile audioFile,
            Long albumId,
            Integer trackNumber);

    
	SongDTO uploadCover(Long songId, MultipartFile image);


	List<SongDTO> getMySongs();
	
	void addSongToAlbum(Long songId, Long albumId, Integer trackNumber);

	List<SongDTO> getAlbumSongs(Long albumId);
	
	void removeFromAlbum(Long songId);

	void reorderTrack(Long songId, Integer newTrackNumber);

	void deleteSong(Long songId);

}
