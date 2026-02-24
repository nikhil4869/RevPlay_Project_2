package com.example.demo.service;

import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface SearchService {

    List<SongDTO> searchByTitle(String title);

    List<SongDTO> searchByArtist(String artistName);

    List<SongDTO> searchByGenre(String genre);
    
    List<SongDTO> searchByYear(Integer year);

    List<SongDTO> searchByAlbum(String albumName);
}