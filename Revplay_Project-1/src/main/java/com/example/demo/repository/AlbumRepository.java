package com.example.demo.repository;

import com.example.demo.entity.Album;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByArtist(User artist);

}
