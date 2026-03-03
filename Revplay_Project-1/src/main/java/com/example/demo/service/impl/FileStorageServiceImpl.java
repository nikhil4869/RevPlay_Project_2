package com.example.demo.service.impl;

import com.example.demo.exception.FileStorageException;
import com.example.demo.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String baseDir = System.getProperty("user.dir");
    private final String audioUploadDir = baseDir + File.separator + "uploads" + File.separator + "audio";
    private final String imageUploadDir = baseDir + File.separator + "uploads" + File.separator + "images";
    private static final Logger logger = LogManager.getLogger(FileStorageServiceImpl.class);
    @Override
    public String storeAudio(MultipartFile file) {

        logger.debug("Storing audio file. Original name: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("Audio upload failed - File is empty");
            throw new FileStorageException("File is empty");
        }

        try {

            File directory = new File(audioUploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                logger.info("Audio upload directory created: {} - Success: {}",
                        audioUploadDir, created);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(directory, fileName);

            file.transferTo(dest);

            logger.info("Audio file stored successfully: {}", fileName);

            return "/audio/" + fileName;

        } catch (IOException e) {
            logger.error("Failed to store audio file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to store file");
        }
    }

    @Override
    public String storeImage(MultipartFile file) {

        logger.debug("Storing image file. Original name: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("Image upload failed - File is empty");
            throw new FileStorageException("Image file is empty");
        }

        try {

            File directory = new File(imageUploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                logger.info("Image upload directory created: {} - Success: {}",
                        imageUploadDir, created);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(directory, fileName);

            file.transferTo(dest);

            logger.info("Image file stored successfully: {}", fileName);

            return "/images/" + fileName;

        } catch (IOException e) {
            logger.error("Failed to store image file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to store image");
        }
    }
    @Override
    public void deleteFile(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            logger.debug("Delete file skipped - Path is null or blank");
            return;
        }

        try {

            String cleanedPath = filePath.startsWith("/")
                    ? filePath.substring(1)
                    : filePath;

            File file = new File(baseDir + File.separator + "uploads" + File.separator + cleanedPath);

            if (file.exists()) {
                boolean deleted = file.delete();
                logger.info("File deletion attempt: {} - Success: {}", cleanedPath, deleted);
            } else {
                logger.warn("File deletion skipped - File does not exist: {}", cleanedPath);
            }

        } catch (Exception e) {
            logger.error("Error occurred while deleting file: {}", filePath, e);
        }
    }
}