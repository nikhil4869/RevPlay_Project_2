package com.example.demo.repository;

import com.example.demo.entity.ArtistProfile;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<ArtistProfile, Long> {

    // find profile by logged-in user
    Optional<ArtistProfile> findByUser(User user);
<<<<<<< HEAD
=======
    
    List<ArtistProfile> findByArtistNameContainingIgnoreCase(String keyword);

>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

}
