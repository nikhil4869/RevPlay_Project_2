package com.example.demo.service;


import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.dto.user.UserDashboardDTO;

public interface UserService {

    void deactivateMyAccount();

<<<<<<< HEAD
=======
    UserProfileDTO getMyProfile();

    UserProfileDTO updateProfile(UserProfileDTO dto);

    UserProfileDTO uploadProfileImage(MultipartFile image);
    
    UserDashboardDTO getDashboardStats();
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}
