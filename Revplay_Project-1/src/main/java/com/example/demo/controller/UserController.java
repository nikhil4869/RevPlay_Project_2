package com.example.demo.controller;

import com.example.demo.dto.user.UserProfileDTO;
import com.example.demo.dto.user.UpdateProfileDTO;
import com.example.demo.dto.user.UserStatsDTO;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserProfileDTO getProfile() {
        return userService.getMyProfile();
    }

    @PutMapping("/profile")
    public String updateProfile(@RequestBody UpdateProfileDTO dto) {
        userService.updateMyProfile(dto);
        return "Profile updated successfully";
    }

    @GetMapping("/stats")
    public UserStatsDTO getStats() {
        return userService.getMyStats();
    }

    @PutMapping("/deactivate")
    public String deactivateAccount() {
        userService.deactivateMyAccount();
        return "Account deactivated successfully";
    }
}