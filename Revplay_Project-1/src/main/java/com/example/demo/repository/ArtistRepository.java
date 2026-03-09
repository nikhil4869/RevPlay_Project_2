package com.example.demo.repository;

import com.example.demo.entity.ArtistProfile;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<ArtistProfile, Long> {

    // find profile by logged-in user
    Optional<ArtistProfile> findByUser(User user);
    
    List<ArtistProfile> findByArtistNameContainingIgnoreCase(String keyword);


}
