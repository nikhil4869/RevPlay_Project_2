package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeAudio(MultipartFile file);
    String storeImage(MultipartFile file);
    void deleteFile(String filePath);

}
