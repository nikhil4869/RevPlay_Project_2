package com.example.demo.service;

import java.util.List;
import com.example.demo.dto.music.SongDTO;
import com.example.demo.entity.Song;
import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.dto.music.AlbumDTO;

public interface SearchService {

    List<SongDTO> searchSongs(String keyword);

    List<ArtistDTO> searchArtists(String keyword);

    List<AlbumDTO> searchAlbums(String keyword);
    
    List<SongDTO> searchByYear(Integer year);
    
    List<String> getAllGenres();

    List<SongDTO> searchSongsByGenre(String genre);


}
