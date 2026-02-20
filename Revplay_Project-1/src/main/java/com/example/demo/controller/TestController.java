package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.util.SecurityUtil;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-error")
    public String testError() {
        throw new ResourceNotFoundException("Exception working correctly");
    }
    
    @GetMapping("/secure")
    public String secure() {
        return "Secured endpoint accessed";
    }
    
    @GetMapping("/me")
    public String me() {
        return SecurityUtil.getCurrentUserEmail();
    }


}
