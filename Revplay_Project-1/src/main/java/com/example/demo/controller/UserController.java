package com.example.demo.controller;

<<<<<<< HEAD
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;
=======
import com.example.demo.dto.music.UserProfileDTO;
import com.example.demo.dto.user.UserDashboardDTO;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/deactivate")
    public String deactivateAccount() {
        userService.deactivateMyAccount();
        return "Account deactivated successfully";
    }
<<<<<<< HEAD
=======
    
    @GetMapping("/profile")
    public UserProfileDTO getProfile() {
        return userService.getMyProfile();
    }

    @PutMapping("/profile")
    public UserProfileDTO updateProfile(@RequestBody UserProfileDTO dto) {
        return userService.updateProfile(dto);
    }

    @PostMapping("/profile/image")
    public UserProfileDTO uploadProfileImage(@RequestParam MultipartFile image) {
        return userService.uploadProfileImage(image);
    }

    @GetMapping("/dashboard")
    public UserDashboardDTO dashboard() {
        return userService.getDashboardStats();
    }
>>>>>>> daf7a6e101d383c386b27942eb94de04b50ebd08
}
