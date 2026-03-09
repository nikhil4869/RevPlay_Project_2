package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.music.ArtistDTO;

public interface ArtistService {

    // create artist profile
    ArtistDTO createProfile(ArtistDTO dto);

    // update artist profile
    ArtistDTO updateProfile(ArtistDTO dto);

    // get logged-in artist profile
    ArtistDTO getMyProfile();

    // view artist profile by id (public view)
    ArtistDTO getArtistProfile(Long artistId);
    
    ArtistDTO uploadProfileImage(Long profileId, MultipartFile image);

    ArtistDTO uploadBannerImage(Long profileId, MultipartFile image);

}
