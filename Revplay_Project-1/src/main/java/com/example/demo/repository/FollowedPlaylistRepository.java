package com.example.demo.repository;

import com.example.demo.entity.FollowedPlaylist;
import com.example.demo.entity.Playlist;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowedPlaylistRepository extends JpaRepository<FollowedPlaylist, Long> {

    Optional<FollowedPlaylist> findByUserAndPlaylist(User user, Playlist playlist);

    List<FollowedPlaylist> findByUser(User user);

}