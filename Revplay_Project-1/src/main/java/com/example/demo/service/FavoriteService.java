package com.example.demo.service;

<<<<<<< HEAD
import com.example.demo.entity.Song;
=======
import com.example.demo.dto.music.SongDTO;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import java.util.List;

public interface FavoriteService {

<<<<<<< HEAD
    void addToFavorites(Long songId);

    void removeFromFavorites(Long songId);

    List<Song> getMyFavorites();
}
=======
    void addFavorite(Long songId);

    void removeFavorite(Long songId);

    List<SongDTO> getMyFavorites();
}
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
