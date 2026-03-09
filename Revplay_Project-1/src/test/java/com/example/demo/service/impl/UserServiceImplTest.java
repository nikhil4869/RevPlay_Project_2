package com.example.demo.service.impl;


import com.example.demo.dto.user.UserDashboardDTO;
import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.PlayHistoryRepository;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlayHistoryRepository playHistoryRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("Test User");
        when(mockUser.getEmail()).thenReturn("user@example.com");
        when(mockUser.getBio()).thenReturn("My bio");
        when(mockUser.getProfileImage()).thenReturn("/images/profile.jpg");
        when(mockUser.getId()).thenReturn(1L); // Only for reference, never setId()
    }

    // =================== deactivateMyAccount ===================
    @Test
    void deactivateMyAccount_Success() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@example.com");

            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

            userService.deactivateMyAccount();

            verify(mockUser).setEnabled(false);
            verify(userRepository).save(mockUser);
        }
    }

    // =================== getMyProfile ===================
    @Test
    void getMyProfile_Success() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@example.com");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

            UserProfileDTO dto = userService.getMyProfile();

            assertEquals("Test User", dto.getName());
            assertEquals("user@example.com", dto.getEmail());
        }
    }

    // =================== updateProfile ===================
    @Test
    void updateProfile_Success() {
        UserProfileDTO inputDto = new UserProfileDTO();
        inputDto.setName("Updated Name");
        inputDto.setBio("Updated Bio");

        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@example.com");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

            when(userRepository.save(mockUser)).thenReturn(mockUser);

            UserProfileDTO dto = userService.updateProfile(inputDto);

            verify(mockUser).setName("Updated Name");
            verify(mockUser).setBio("Updated Bio");

            assertEquals("Test User", dto.getName()); // Because mock returns original getName()
        }
    }

    // =================== uploadProfileImage ===================
    @Test
    void uploadProfileImage_Success() {
        MultipartFile mockFile = mock(MultipartFile.class);

        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@example.com");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
            when(fileStorageService.storeImage(mockFile)).thenReturn("/images/new.jpg");
            when(userRepository.save(mockUser)).thenReturn(mockUser);

            UserProfileDTO dto = userService.uploadProfileImage(mockFile);

            verify(mockUser).setProfileImage("/images/new.jpg");
            assertEquals("Test User", dto.getName());
        }
    }

    // =================== getDashboardStats ===================
    @Test
    void getDashboardStats_Success() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@example.com");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

            when(favoriteRepository.countByUser(mockUser)).thenReturn(2L);
            when(playlistRepository.countByUser(mockUser)).thenReturn(3L);
            when(playHistoryRepository.countByUser(mockUser)).thenReturn(5L);
            when(playHistoryRepository.getTotalListeningTime(mockUser)).thenReturn(3660L); // 1 hr 1 min

            UserDashboardDTO dto = userService.getDashboardStats();

            assertEquals("1 hr 1 min", dto.getListeningTime());
            assertEquals(2L, dto.getFavorites());
            assertEquals(3L, dto.getPlaylists());
            assertEquals(5L, dto.getRecentlyPlayed());
        }
    }

    // =================== ResourceNotFoundException Scenarios ===================
    @Test
    void deactivateMyAccount_UserNotFound_ThrowsException() {
        try (MockedStatic<SecurityUtil> utilities = mockStatic(SecurityUtil.class)) {
            utilities.when(SecurityUtil::getCurrentUserEmail).thenReturn("missing@example.com");
            when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> userService.deactivateMyAccount());
        }
    }
}