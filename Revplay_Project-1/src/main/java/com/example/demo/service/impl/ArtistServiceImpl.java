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


@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;


    public ArtistServiceImpl(ArtistRepository artistRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
          this.artistRepository = artistRepository;
          this.userRepository = userRepository;
          this.fileStorageService = fileStorageService;
}


    @Override
    public ArtistDTO createProfile(ArtistDTO dto) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (artistRepository.findByUser(user).isPresent()) {
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

        return dto;
    }

    @Override
    public ArtistDTO updateProfile(ArtistDTO dto) {

        String email = SecurityUtil.getCurrentUserEmail();

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
        profile.setProfileImage(dto.getProfileImage());
        profile.setBannerImage(dto.getBannerImage());

        artistRepository.save(profile);

        return dto;
    }

    @Override
    public ArtistDTO getMyProfile() {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ArtistProfile profile = artistRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapToDTO(profile);
    }

    @Override
    public ArtistDTO getArtistProfile(Long artistId) {

        ArtistProfile profile = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        return mapToDTO(profile);
    }
    
    @Override
    public ArtistDTO uploadProfileImage(Long profileId, MultipartFile image) {

        ArtistProfile profile = artistRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }


        String path = fileStorageService.storeImage(image);

        profile.setProfileImage(path);
        artistRepository.save(profile);

        return mapToDTO(profile);
    }

    @Override
    public ArtistDTO uploadBannerImage(Long profileId, MultipartFile image) {

        ArtistProfile profile = artistRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Unauthorized access");
        }


        String path = fileStorageService.storeImage(image);

        profile.setBannerImage(path);
        artistRepository.save(profile);

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

        dto.setProfileImage(profile.getProfileImage());
        dto.setBannerImage(profile.getBannerImage());

        return dto;
    }

}
