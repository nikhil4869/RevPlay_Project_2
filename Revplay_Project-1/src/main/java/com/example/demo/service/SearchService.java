package com.example.demo.service;

<<<<<<< HEAD
import com.example.demo.dto.music.SongDTO;
import java.util.List;

public interface SearchService {

    List<SongDTO> searchByTitle(String title);

    List<SongDTO> searchByArtist(String artistName);

    List<SongDTO> searchByGenre(String genre);
}
=======
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
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
