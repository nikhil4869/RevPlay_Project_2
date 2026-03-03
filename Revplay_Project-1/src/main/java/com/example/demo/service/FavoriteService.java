package com.example.demo.service;

import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface FavoriteService {

    void addFavorite(Long songId);

    void removeFavorite(Long songId);

    List<SongDTO> getMyFavorites();
}
