package com.example.demo.service.impl;

import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import com.example.demo.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.user.UserDashboardDTO;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.FavoriteRepository;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final FavoriteRepository favoriteRepository;
    private final PlaylistRepository playlistRepository;
    private final PlayHistoryRepository playHistoryRepository;





    public UserServiceImpl(UserRepository userRepository, FileStorageService fileStorageService,
			FavoriteRepository favoriteRepository, PlaylistRepository playlistRepository,
			PlayHistoryRepository playHistoryRepository) {
		this.userRepository = userRepository;
		this.fileStorageService = fileStorageService;
		this.favoriteRepository = favoriteRepository;
		this.playlistRepository = playlistRepository;
		this.playHistoryRepository = playHistoryRepository;
	}

	@Override
    public void deactivateMyAccount() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(false);

        userRepository.save(user);
    }
    
    @Override
    public UserProfileDTO getMyProfile() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToDTO(user);
    }

    @Override
    public UserProfileDTO updateProfile(UserProfileDTO dto) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(dto.getName());
        user.setBio(dto.getBio());

        userRepository.save(user);

        return mapToDTO(user);
    }

    @Override
    public UserProfileDTO uploadProfileImage(MultipartFile image) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String imagePath = fileStorageService.storeImage(image);

        user.setProfileImage(imagePath);

        userRepository.save(user);

        return mapToDTO(user);
    }
    
    @Override
    public UserDashboardDTO getDashboardStats() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long favorites = favoriteRepository.countByUser(user);
        long playlists = playlistRepository.countByUser(user);
        long recent = playHistoryRepository.countByUser(user);

        long totalSeconds = playHistoryRepository.getTotalListeningTime(user);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        String listeningTime = hours + " hr " + minutes + " min";

        return new UserDashboardDTO(
                favorites,
                playlists,
                listeningTime,
                recent
        );
    }
    
    private UserProfileDTO mapToDTO(User user) {
        return new UserProfileDTO(
                user.getName(),
                user.getEmail(),
                user.getBio(),
                user.getProfileImage()
        );
    }

}
