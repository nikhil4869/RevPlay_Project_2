package com.example.demo.serviceImplTest;

import com.example.demo.exception.FileStorageException;
import com.example.demo.service.impl.FileStorageServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceImplTest {

    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir;   // temporary directory for safe testing

    @BeforeEach
    void setup() {
        fileStorageService = new FileStorageServiceImpl();
    }

    // ---------------- STORE AUDIO SUCCESS ----------------
    @Test
    void storeAudio_Success() throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("song.mp3");

        String result = fileStorageService.storeAudio(file);

        assertTrue(result.startsWith("/audio/"));
    }

    // ---------------- STORE AUDIO EMPTY ----------------
    @Test
    void storeAudio_EmptyFile() {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(FileStorageException.class,
                () -> fileStorageService.storeAudio(file));
    }

    // ---------------- STORE AUDIO IO EXCEPTION ----------------
    @Test
    void storeAudio_IOException() throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("song.mp3");

        doThrow(new IOException())
                .when(file).transferTo(any(File.class));

        assertThrows(FileStorageException.class,
                () -> fileStorageService.storeAudio(file));
    }

    // ---------------- STORE IMAGE SUCCESS ----------------
    @Test
    void storeImage_Success() throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        String result = fileStorageService.storeImage(file);

        assertTrue(result.startsWith("/images/"));
    }

    // ---------------- STORE IMAGE EMPTY ----------------
    @Test
    void storeImage_EmptyFile() {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(FileStorageException.class,
                () -> fileStorageService.storeImage(file));
    }

    // ---------------- STORE IMAGE IO EXCEPTION ----------------
    @Test
    void storeImage_IOException() throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        doThrow(new IOException())
                .when(file).transferTo(any(File.class));

        assertThrows(FileStorageException.class,
                () -> fileStorageService.storeImage(file));
    }

    // ---------------- DELETE FILE NULL ----------------
    @Test
    void deleteFile_NullPath() {

        assertDoesNotThrow(() ->
                fileStorageService.deleteFile(null));
    }

    // ---------------- DELETE FILE SUCCESS ----------------
    @Test
    void deleteFile_Success() throws Exception {

        // Create dummy file manually
        String baseDir = System.getProperty("user.dir");
        File folder = new File(baseDir + File.separator + "uploads" + File.separator + "audio");
        folder.mkdirs();

        File file = new File(folder, "test.txt");
        file.createNewFile();

        assertTrue(file.exists());

        fileStorageService.deleteFile("/audio/test.txt");

        assertFalse(file.exists());
    }
}