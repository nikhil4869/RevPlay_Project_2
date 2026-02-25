package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.music.AlbumDTO;
import java.time.LocalDate;
import java.util.List;

public interface AlbumService {

    AlbumDTO createAlbum(String name,
                      String description,
                      LocalDate releaseDate);

    AlbumDTO uploadCover(Long albumId, MultipartFile image);

    List<AlbumDTO> getMyAlbums();
<<<<<<< HEAD
=======
    
    AlbumDTO getAlbumDetails(Long albumId);
    
    List<AlbumDTO> getAllAlbums();


>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}
