package com.example.demo.service.impl;


import com.example.demo.dto.music.ArtistDTO;
import com.example.demo.entity.ArtistProfile;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.util.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private User user;
    private ArtistProfile profile;
    private ArtistDTO dto;

    @BeforeEach
    void setup() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("artist@test.com");

        profile = new ArtistProfile();
        ReflectionTestUtils.setField(profile, "id", 10L);
        profile.setUser(user);

        dto = new ArtistDTO();
        dto.setArtistName("Artist");
        dto.setBio("Bio");
        dto.setGenre("Pop");
    }

    
    // CREATE PROFILE
    
    @Test
    void createProfile_Success() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(user));

            when(artistRepository.findByUser(user))
                    .thenReturn(Optional.empty());

            ArtistDTO result = artistService.createProfile(dto);

            assertEquals("Artist", result.getArtistName());
            verify(artistRepository).save(any(ArtistProfile.class));
        }
    }

    @Test
    void createProfile_UserNotFound() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> artistService.createProfile(dto));
        }
    }

    @Test
    void createProfile_ProfileAlreadyExists() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(user));

            when(artistRepository.findByUser(user))
                    .thenReturn(Optional.of(profile));

            assertThrows(BadRequestException.class,
                    () -> artistService.createProfile(dto));
        }
    }

    
    // UPDATE PROFILE
    

    @Test
    void updateProfile_Success() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(user));

            when(artistRepository.findByUser(user))
                    .thenReturn(Optional.of(profile));

            ArtistDTO result = artistService.updateProfile(dto);

            assertEquals("Artist", result.getArtistName());
            verify(artistRepository).save(profile);
        }
    }

    @Test
    void updateProfile_ProfileNotFound() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(user));

            when(artistRepository.findByUser(user))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> artistService.updateProfile(dto));
        }
    }

    
    // GET MY PROFILE
    

    @Test
    void getMyProfile_Success() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(user));

            when(artistRepository.findByUser(user))
                    .thenReturn(Optional.of(profile));

            ArtistDTO result = artistService.getMyProfile();

            assertNotNull(result);
        }
    }

    // =========================
    // GET ARTIST BY ID
    // =========================

    @Test
    void getArtistProfile_Success() {
        when(artistRepository.findById(10L))
                .thenReturn(Optional.of(profile));

        ArtistDTO result = artistService.getArtistProfile(10L);

        assertNotNull(result);
    }

    @Test
    void getArtistProfile_NotFound() {
        when(artistRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> artistService.getArtistProfile(10L));
    }

    // =========================
    // UPLOAD PROFILE IMAGE
    // =========================

    @Test
    void uploadProfileImage_Success() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(artistRepository.findById(10L))
                    .thenReturn(Optional.of(profile));

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(user));

            when(fileStorageService.storeImage(any()))
                    .thenReturn("image/path.jpg");

            MockMultipartFile file =
                    new MockMultipartFile("file", "img.jpg",
                            "image/jpeg", "data".getBytes());

            ArtistDTO result =
                    artistService.uploadProfileImage(10L, file);

            assertEquals("image/path.jpg", result.getProfileImage());
        }
    }

    @Test
    void uploadProfileImage_Unauthorized() {
        try (MockedStatic<SecurityUtil> security = mockStatic(SecurityUtil.class)) {

            User anotherUser = new User();
            ReflectionTestUtils.setField(anotherUser, "id", 99L);

            security.when(SecurityUtil::getCurrentUserEmail)
                    .thenReturn("artist@test.com");

            when(artistRepository.findById(10L))
                    .thenReturn(Optional.of(profile));

            when(userRepository.findByEmail("artist@test.com"))
                    .thenReturn(Optional.of(anotherUser));

            MockMultipartFile file =
                    new MockMultipartFile("file", "img.jpg",
                            "image/jpeg", "data".getBytes());

            assertThrows(BadRequestException.class,
                    () -> artistService.uploadProfileImage(10L, file));
        }
    }
}
