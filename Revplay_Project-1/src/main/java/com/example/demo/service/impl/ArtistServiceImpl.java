package com.example.demo.service.impl;

import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.entity.ArtistProfile;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ArtistService;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.service.FileStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private static final Logger logger = LogManager.getLogger(ArtistServiceImpl.class);

    public ArtistServiceImpl(ArtistRepository artistRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
          this.artistRepository = artistRepository;
          this.userRepository = userRepository;
          this.fileStorageService = fileStorageService;
}


    @Override
    public ArtistDTO createProfile(ArtistDTO dto) {

        logger.debug("Creating artist profile");

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while creating artist profile. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (artistRepository.findByUser(user).isPresent()) {
            logger.warn("Artist profile already exists for user id: {}", user.getId());
            throw new BadRequestException("Artist profile already exists");
        }

        ArtistProfile profile = new ArtistProfile();
        profile.setArtistName(dto.getArtistName());
        profile.setBio(dto.getBio());
        profile.setGenre(dto.getGenre());
        profile.setInstagram(dto.getInstagram());
        profile.setTwitter(dto.getTwitter());
        profile.setYoutube(dto.getYoutube());
        profile.setWebsite(dto.getWebsite());
        profile.setUser(user);

        artistRepository.save(profile);

        logger.info("Artist profile created successfully. Profile id: {}, User id: {}",
                profile.getId(), user.getId());

        return dto;
    }

    @Override
    public ArtistDTO updateProfile(ArtistDTO dto) {

        logger.debug("Updating artist profile");

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while updating profile. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        ArtistProfile profile = artistRepository.findByUser(user)
                .orElseThrow(() -> {
                    logger.error("Artist profile not found for user id: {}", user.getId());
                    return new ResourceNotFoundException("Profile not found");
                });

        profile.setArtistName(dto.getArtistName());
        profile.setBio(dto.getBio());
        profile.setGenre(dto.getGenre());
        profile.setInstagram(dto.getInstagram());
        profile.setTwitter(dto.getTwitter());
        profile.setYoutube(dto.getYoutube());
        profile.setWebsite(dto.getWebsite());
<<<<<<< HEAD
        profile.setProfileImage(dto.getProfileImage());
        profile.setBannerImage(dto.getBannerImage());
=======
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

        artistRepository.save(profile);

        logger.info("Artist profile updated successfully. Profile id: {}", profile.getId());

        return dto;
    }

    @Override
    public ArtistDTO getMyProfile() {

        logger.debug("Fetching current artist profile");

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while fetching profile. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        return artistRepository.findByUser(user)
                .map(profile -> {
                    logger.info("Artist profile retrieved successfully. Profile id: {}", profile.getId());
                    return mapToDTO(profile);
                })
                .orElse(null);
    }
    @Override
    public ArtistDTO getArtistProfile(Long artistId) {

        logger.debug("Fetching artist profile by id: {}", artistId);

        ArtistProfile profile = artistRepository.findById(artistId)
                .orElseThrow(() -> {
                    logger.error("Artist profile not found with id: {}", artistId);
                    return new ResourceNotFoundException("Artist not found");
                });

        logger.info("Artist profile retrieved successfully. Profile id: {}", artistId);

        return mapToDTO(profile);
    }
    
    @Override
    public ArtistDTO uploadProfileImage(Long profileId, MultipartFile image) {

        logger.debug("Uploading profile image for profile id: {}", profileId);

        ArtistProfile profile = artistRepository.findById(profileId)
                .orElseThrow(() -> {
                    logger.error("Profile not found with id: {}", profileId);
                    return new ResourceNotFoundException("Profile not found");
                });

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while uploading profile image. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!profile.getUser().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized profile image upload attempt. Profile id: {}, User id: {}",
                    profileId, currentUser.getId());
            throw new BadRequestException("Unauthorized access");
        }

        String path = fileStorageService.storeImage(image);
        profile.setProfileImage(path);
        artistRepository.save(profile);

        logger.info("Profile image uploaded successfully. Profile id: {}", profileId);

        return mapToDTO(profile);
    }

    @Override
    public ArtistDTO uploadBannerImage(Long profileId, MultipartFile image) {

        logger.debug("Uploading banner image for profile id: {}", profileId);

        ArtistProfile profile = artistRepository.findById(profileId)
                .orElseThrow(() -> {
                    logger.error("Profile not found with id: {}", profileId);
                    return new ResourceNotFoundException("Profile not found");
                });

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while uploading banner image. Email: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!profile.getUser().getId().equals(currentUser.getId())) {
            logger.warn("Unauthorized banner upload attempt. Profile id: {}, User id: {}",
                    profileId, currentUser.getId());
            throw new BadRequestException("Unauthorized access");
        }

        String path = fileStorageService.storeImage(image);
        profile.setBannerImage(path);
        artistRepository.save(profile);

        logger.info("Banner image uploaded successfully. Profile id: {}", profileId);

        return mapToDTO(profile);
    }


    private ArtistDTO mapToDTO(ArtistProfile profile) {

        ArtistDTO dto = new ArtistDTO();
        dto.setArtistName(profile.getArtistName());
        dto.setBio(profile.getBio());
        dto.setGenre(profile.getGenre());
        dto.setInstagram(profile.getInstagram());
        dto.setTwitter(profile.getTwitter());
        dto.setYoutube(profile.getYoutube());
        dto.setWebsite(profile.getWebsite());

        dto.setId(profile.getId());
        dto.setProfileImage(profile.getProfileImage());
        dto.setBannerImage(profile.getBannerImage());

        return dto;
    }

}
