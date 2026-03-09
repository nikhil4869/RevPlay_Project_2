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

    private static final Logger logger = LogManager.getLogger(ArtistServiceImpl.class);

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ArtistServiceImpl(ArtistRepository artistRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
          this.artistRepository = artistRepository;
          this.userRepository = userRepository;
          this.fileStorageService = fileStorageService;

          logger.info("ArtistServiceImpl initialized");
}

    @Override
    public ArtistDTO createProfile(ArtistDTO dto) {

        logger.info("Creating artist profile");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (artistRepository.findByUser(user).isPresent()) {

            logger.warn("Artist profile already exists for userId={}", user.getId());

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

        logger.info("Artist profile created successfully for userId={}", user.getId());

        return dto;
    }

    @Override
    public ArtistDTO updateProfile(ArtistDTO dto) {

        logger.info("Updating artist profile");

        String email = SecurityUtil.getCurrentUserEmail();

        logger.debug("Current user email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ArtistProfile profile = artistRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        profile.setArtistName(dto.getArtistName());
        profile.setBio(dto.getBio());
        profile.setGenre(dto.getGenre());
        profile.setInstagram(dto.getInstagram());
        profile.setTwitter(dto.getTwitter());
        profile.setYoutube(dto.getYoutube());
        profile.setWebsite(dto.getWebsite());

        artistRepository.save(profile);

        logger.info("Artist profile updated successfully for profileId={}", profile.getId());

        return dto;
    }

    @Override
    public ArtistDTO getMyProfile() {

        logger.info("Fetching current user's artist profile");

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return artistRepository.findByUser(user)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public ArtistDTO getArtistProfile(Long artistId) {

        logger.info("Fetching artist profile for artistId={}", artistId);

        ArtistProfile profile = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        return mapToDTO(profile);
    }

    @Override
    public ArtistDTO uploadProfileImage(Long profileId, MultipartFile image) {

        logger.info("Uploading profile image for profileId={}", profileId);

        ArtistProfile profile = artistRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!profile.getUser().getId().equals(currentUser.getId())) {

            logger.warn("Unauthorized profile image upload attempt by userId={} for profileId={}",
                    currentUser.getId(), profileId);

            throw new BadRequestException("Unauthorized access");
        }

        String path = fileStorageService.storeImage(image);

        logger.debug("Profile image stored at path={}", path);

        profile.setProfileImage(path);
        artistRepository.save(profile);

        logger.info("Profile image uploaded successfully for profileId={}", profileId);

        return mapToDTO(profile);
    }

    @Override
    public ArtistDTO uploadBannerImage(Long profileId, MultipartFile image) {

        logger.info("Uploading banner image for profileId={}", profileId);

        ArtistProfile profile = artistRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        String email = SecurityUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!profile.getUser().getId().equals(currentUser.getId())) {

            logger.warn("Unauthorized banner upload attempt by userId={} for profileId={}",
                    currentUser.getId(), profileId);

            throw new BadRequestException("Unauthorized access");
        }

        String path = fileStorageService.storeImage(image);

        logger.debug("Banner image stored at path={}", path);

        profile.setBannerImage(path);
        artistRepository.save(profile);

        logger.info("Banner image uploaded successfully for profileId={}", profileId);

        return mapToDTO(profile);
    }

    private ArtistDTO mapToDTO(ArtistProfile profile) {

        logger.debug("Mapping ArtistProfile to DTO profileId={}", profile.getId());

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