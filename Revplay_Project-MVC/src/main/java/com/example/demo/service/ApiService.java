package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8080";

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> login(String email, String password) {

        String url = BASE_URL + "/auth/login";

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map<String, String> result = new HashMap<>();
        result.put("token", (String) response.getBody().get("token"));
        result.put("role", (String) response.getBody().get("role"));

        return result;
    }
    
    public String register(Map<String, String> requestData) {

        String url = BASE_URL + "/auth/register";

        return restTemplate.postForObject(url, requestData, String.class);
    }
    
    public String resetPassword(Map<String, String> requestData) {

        String url = BASE_URL + "/auth/forgot-password";

        return restTemplate.postForObject(url, requestData, String.class);
    }
    

}