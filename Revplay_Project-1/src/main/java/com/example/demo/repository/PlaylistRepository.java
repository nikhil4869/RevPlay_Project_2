package com.example.demo.repository;

import com.example.demo.entity.Playlist;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByListener(User listener);

    List<Playlist> findByIsPublicTrue();   // new
=======
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByUser(User user);

    List<Playlist> findByIsPublicTrue();
    
    long countByUser(User user);
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}