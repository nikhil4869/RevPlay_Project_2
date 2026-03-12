package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

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

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map<String, String> result = new HashMap<>();
            result.put("token", (String) response.getBody().get("token"));
            result.put("role", (String) response.getBody().get("role"));

            return result;
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException(extractErrorMessage(e));
        }
    }
    
    public String register(Map<String, String> requestData) {
        String url = BASE_URL + "/auth/register";
        try {
            return restTemplate.postForObject(url, requestData, String.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException(extractErrorMessage(e));
        }
    }
    
    public String resetPassword(Map<String, String> requestData) {
        String url = BASE_URL + "/auth/forgot-password";
        try {
            return restTemplate.postForObject(url, requestData, String.class);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException(extractErrorMessage(e));
        }
    }

    private String extractErrorMessage(HttpStatusCodeException e) {
        String responseBody = e.getResponseBodyAsString();
        if (responseBody != null && responseBody.contains("\"message\":\"")) {
            int start = responseBody.indexOf("\"message\":\"") + 11;
            int end = responseBody.indexOf("\"", start);
            if (start > 10 && end > start) {
                return responseBody.substring(start, end);
            }
        }
        return e.getStatusText();
    }
    

}