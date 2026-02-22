package com.example.demo.service;

import com.example.demo.entity.Song;
import java.util.List;

public interface FavoriteService {

    void addToFavorites(Long songId);

    void removeFromFavorites(Long songId);

    List<Song> getMyFavorites();
}