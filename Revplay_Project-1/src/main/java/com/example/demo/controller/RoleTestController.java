package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/artist-only")
    public String artistOnly() {
        return "Artist content accessed";
    }
    
    @GetMapping("/user-only")
    public String userOnly() {
        return "User content accessed";
    }
}
