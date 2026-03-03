package com.example.demo.service;


import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.dto.user.UserDashboardDTO;

public interface UserService {

    void deactivateMyAccount();

    UserProfileDTO getMyProfile();

    UserProfileDTO updateProfile(UserProfileDTO dto);

    UserProfileDTO uploadProfileImage(MultipartFile image);
    
    UserDashboardDTO getDashboardStats();
}
