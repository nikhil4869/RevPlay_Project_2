package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.user.DashboardDTO;
import com.example.demo.dto.user.UpdateProfileDTO;
import com.example.demo.dto.user.UserProfileDTO;

import com.example.demo.service.UserService;

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



    @PutMapping("/deactivate")
    public String deactivateAccount() {
        userService.deactivateMyAccount();
        return "Account deactivated successfully";
    }
    
    @GetMapping("/dashboard")
    public DashboardDTO getDashboard() {
        return userService.getMyDashboard();
    }
}