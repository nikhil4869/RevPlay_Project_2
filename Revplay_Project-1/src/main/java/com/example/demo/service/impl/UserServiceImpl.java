package com.example.demo.service.impl;

import com.example.demo.dto.user.UserProfileDTO;
import com.example.demo.dto.user.UpdateProfileDTO;
import com.example.demo.dto.user.UserStatsDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final FavoriteRepository favoriteRepository;

    public UserServiceImpl(UserRepository userRepository,
            SongRepository songRepository,
            AlbumRepository albumRepository,
            FavoriteRepository favoriteRepository) {
this.userRepository = userRepository;
this.songRepository = songRepository;
this.albumRepository = albumRepository;
this.favoriteRepository = favoriteRepository;
}

    @Override
    public UserProfileDTO getMyProfile() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getRole().getName()
        );
    }

    @Override
    public void updateMyProfile(UpdateProfileDTO dto) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(dto.getName());
        user.setDateOfBirth(dto.getDateOfBirth());

        userRepository.save(user);
    }

    @Override
    public UserStatsDTO getMyStats() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long totalSongs = songRepository.countByArtist(user);
        long totalAlbums = albumRepository.countByArtist(user);

        long totalLiked = favoriteRepository.countByListener(user);

        return new UserStatsDTO(totalSongs, totalLiked, totalAlbums);
    }

    @Override
    public void deactivateMyAccount() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(false);

        userRepository.save(user);
    }
}