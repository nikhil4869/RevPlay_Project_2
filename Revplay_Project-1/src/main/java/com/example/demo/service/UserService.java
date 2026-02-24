package com.example.demo.service;

import com.example.demo.dto.user.UserProfileDTO;
import com.example.demo.dto.user.DashboardDTO;
import com.example.demo.dto.user.UpdateProfileDTO;


public interface UserService {

    UserProfileDTO getMyProfile();

    void updateMyProfile(UpdateProfileDTO dto);

  

    void deactivateMyAccount();
    
    DashboardDTO getMyDashboard();
}