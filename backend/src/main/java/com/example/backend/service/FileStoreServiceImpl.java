package com.example.backend.service;

import com.example.backend.exception.FileManagerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStoreServiceImpl implements FileStoreService {
    @Value("${upload.path}")
    private String uploadPath;

    private static final Long MAX_FILE_SIZE = 5L * 1024L * 1024L;

    // Я не знаю как лучше, пусть пока просто падает
    @PostConstruct
    private void init() throws IOException {
        Path path = Paths.get(uploadPath);
        Files.createDirectories(path);
        log.info("Created a directory to store images");
    }

    @Override
    public List<String> saveFiles(List<MultipartFile> files) throws IllegalArgumentException, FileManagerException {
        if (files == null || files.isEmpty()) {
            return null;
        }

        if (files.size() > 6) {
            throw new IllegalArgumentException("Exceeded the allowed number of files, no more than six.");
        }

        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("The file " + file.getOriginalFilename() + "is larger than 5 Mb");
            }
        }

        List<String> links = new ArrayList<>();

        log.info("Saving files ...");

        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null) {
                continue;
            }

            String uuidFileName = UUID.randomUUID().toString();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
            String finalName = uuidFileName + extension;

            checkFileExtension(extension);

            try {
                Path filePath = Paths.get(uploadPath, finalName);
                file.transferTo(Files.createFile(filePath));
            } catch (IOException e) {
                throw new FileManagerException("An error occurred while saving files");
            }

            links.add(finalName);
        }

        return links;
    }

    @Override
    public void deleteFiles(List<String> links) throws FileManagerException {
        if (links == null || links.isEmpty()) {
            return;
        }

        log.info("Deleting files ...");

        try {
            for (String link : links) {
                Path filePath = Paths.get(uploadPath, link);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
        } catch (IOException e) {
            throw new FileManagerException("An error occurred while deleting files");
        }
    }

    private void checkFileExtension(String extension) throws IllegalArgumentException {
        if (!extension.matches("\\.(jpg|jpeg|png)")) {
            throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG formats are allowed");
        }
    }
}
