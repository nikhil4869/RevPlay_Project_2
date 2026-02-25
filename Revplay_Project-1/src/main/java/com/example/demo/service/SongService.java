package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
<<<<<<< HEAD
=======

import com.example.demo.dto.analytics.SongFavoriteStatsDTO;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface SongService {

<<<<<<< HEAD
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
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
