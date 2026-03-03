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

    private final String baseDir = System.getProperty("user.dir");
    private final String audioUploadDir = baseDir + File.separator + "uploads" + File.separator + "audio";
    private final String imageUploadDir = baseDir + File.separator + "uploads" + File.separator + "images";

    @Override
    public String storeAudio(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        try {

            File directory = new File(audioUploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(directory, fileName);

            file.transferTo(dest);

            return "/audio/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();   // IMPORTANT for debugging
            throw new FileStorageException("Failed to store file");
        }
    }

    @Override
    public String storeImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileStorageException("Image file is empty");
        }

        try {

            File directory = new File(imageUploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(directory, fileName);

            file.transferTo(dest);

            return "/images/" + fileName;

        } catch (IOException e) {
            e.printStackTrace();   // IMPORTANT
            throw new FileStorageException("Failed to store image");
        }
    }

    @Override
    public void deleteFile(String filePath) {

        if (filePath == null || filePath.isBlank()) return;

        try {

            String cleanedPath = filePath.startsWith("/")
                    ? filePath.substring(1)
                    : filePath;

            File file = new File(baseDir + File.separator + "uploads" + File.separator + cleanedPath);

            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}