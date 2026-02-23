package com.example.demo.service;

import com.example.demo.dto.user.UserProfileDTO;
import com.example.demo.dto.user.UpdateProfileDTO;
import com.example.demo.dto.user.UserStatsDTO;

public interface UserService {

    UserProfileDTO getMyProfile();

    void updateMyProfile(UpdateProfileDTO dto);

    UserStatsDTO getMyStats();

    void deactivateMyAccount();
}