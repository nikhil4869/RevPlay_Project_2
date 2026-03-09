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

    private static final Logger logger = LogManager.getLogger(FileStorageServiceImpl.class);

    private final String baseDir = System.getProperty("user.dir");
    private final String audioUploadDir = baseDir + File.separator + "uploads" + File.separator + "audio";
    private final String imageUploadDir = baseDir + File.separator + "uploads" + File.separator + "images";

    @Override
    public String storeAudio(MultipartFile file) {

        logger.info("Audio upload requested");

        if (file.isEmpty()) {
            logger.warn("Audio upload failed: file is empty");
            throw new FileStorageException("File is empty");
        }

        try {

            File directory = new File(audioUploadDir);
            if (!directory.exists()) {
                logger.debug("Audio directory not found. Creating directory={}", audioUploadDir);
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(directory, fileName);

            logger.debug("Saving audio file name={}", fileName);

            file.transferTo(dest);

            logger.info("Audio file stored successfully path={}", dest.getAbsolutePath());

            return "/audio/" + fileName;

        } catch (IOException e) {

            logger.error("Failed to store audio file", e);
            e.printStackTrace();   // IMPORTANT for debugging

            throw new FileStorageException("Failed to store file");
        }
    }

    @Override
    public String storeImage(MultipartFile file) {

        logger.info("Image upload requested");

        if (file.isEmpty()) {
            logger.warn("Image upload failed: file is empty");
            throw new FileStorageException("Image file is empty");
        }

        try {

            File directory = new File(imageUploadDir);
            if (!directory.exists()) {
                logger.debug("Image directory not found. Creating directory={}", imageUploadDir);
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(directory, fileName);

            logger.debug("Saving image file name={}", fileName);

            file.transferTo(dest);

            logger.info("Image file stored successfully path={}", dest.getAbsolutePath());

            return "/images/" + fileName;

        } catch (IOException e) {

            logger.error("Failed to store image file", e);
            e.printStackTrace();   // IMPORTANT

            throw new FileStorageException("Failed to store image");
        }
    }

    @Override
    public void deleteFile(String filePath) {

        logger.info("File delete requested path={}", filePath);

        if (filePath == null || filePath.isBlank()) {
            logger.warn("File delete skipped: path is null or blank");
            return;
        }

        try {

            String cleanedPath = filePath.startsWith("/")
                    ? filePath.substring(1)
                    : filePath;

            File file = new File(baseDir + File.separator + "uploads" + File.separator + cleanedPath);

            logger.debug("Resolved file path={}", file.getAbsolutePath());

            if (file.exists()) {
                file.delete();
                logger.info("File deleted successfully path={}", file.getAbsolutePath());
            } else {
                logger.warn("File delete skipped: file not found path={}", file.getAbsolutePath());
            }

        } catch (Exception e) {

            logger.error("Error occurred while deleting file", e);
            e.printStackTrace();
        }
    }
}