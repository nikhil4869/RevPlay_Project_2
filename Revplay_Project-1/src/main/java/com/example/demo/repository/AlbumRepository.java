package com.example.demo.repository;

import com.example.demo.entity.Album;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByArtist(User artist);
<<<<<<< HEAD
=======
    
    List<Album> findAll();
    
    List<Album> findByNameContainingIgnoreCase(String keyword);
    
    boolean existsByNameIgnoreCaseAndArtist(String name, User artist);


>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

}
