package com.example.demo.service.impl;

import com.example.demo.exception.FileStorageException;
import com.example.demo.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/audio/";


    @Override
    public String storeAudio(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            return "/audio/" + fileName;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file");
        }
    }
    
    @Override
    public String storeImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileStorageException("Image file is empty");
        }

        try {
            String uploadDir = System.getProperty("user.dir")
                    + "/src/main/resources/static/images/";

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            return "/images/" + fileName;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store image");
        }
    }
    
    @Override
    public void deleteFile(String filePath) {

        if (filePath == null) return;

        try {
            String fullPath = System.getProperty("user.dir")
                    + "/src/main/resources/static"
                    + filePath;

            File file = new File(fullPath);

            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            throw new FileStorageException("Failed to delete file");
        }
    }


}
