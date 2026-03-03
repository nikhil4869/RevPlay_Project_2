package com.example.demo.service;

import com.example.demo.dto.music.ArtistDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArtistProfileApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public ArtistDTO getMyProfile(String token) {

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders(token));

        ResponseEntity<ArtistDTO> response =
                restTemplate.exchange(
                        BASE_URL + "/artist/profile",
                        HttpMethod.GET,
                        entity,
                        ArtistDTO.class
                );

        return response.getBody();
    }

    public void createProfile(String token, ArtistDTO dto) {

        HttpHeaders headers = getHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ArtistDTO> entity = new HttpEntity<>(dto, headers);

        restTemplate.exchange(
                BASE_URL + "/artist/profile",
                HttpMethod.POST,
                entity,
                ArtistDTO.class
        );
    }

    public void updateProfile(String token, ArtistDTO dto) {

        HttpHeaders headers = getHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ArtistDTO> entity = new HttpEntity<>(dto, headers);

        restTemplate.exchange(
                BASE_URL + "/artist/profile",
                HttpMethod.PUT,
                entity,
                ArtistDTO.class
        );
    }

    public void uploadProfileImage(String token,
                                   Long profileId,
                                   MultipartFile file) {

        HttpHeaders headers = getHeaders(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, headers);

        restTemplate.exchange(
                BASE_URL + "/artist/profile/" + profileId + "/image",
                HttpMethod.POST,
                entity,
                String.class
        );
    }

    public void uploadBannerImage(String token,
                                  Long profileId,
                                  MultipartFile file) {

        HttpHeaders headers = getHeaders(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, headers);

        restTemplate.exchange(
                BASE_URL + "/artist/profile/" + profileId + "/banner",
                HttpMethod.POST,
                entity,
                String.class
        );
    }
    
    public void deactivateAccount(String token) {

        HttpHeaders headers = getHeaders(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                BASE_URL + "/user/deactivate",
                HttpMethod.PUT,
                entity,
                String.class
        );
    }
}