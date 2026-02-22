package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

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
}
