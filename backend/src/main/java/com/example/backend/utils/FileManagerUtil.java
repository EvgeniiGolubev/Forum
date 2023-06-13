package com.example.backend.utils;

import com.example.backend.exception.FileManagerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileManagerUtil {
    @Value("${upload.path}")
    private String uploadPath;

    public List<String> saveFilesAndGetLinks(List<MultipartFile> images)
            throws IllegalArgumentException, FileManagerException {

        if (images == null || images.isEmpty()) {
            return null;
        }

        List<String> links = new ArrayList<>();

        try {
            Path path = Paths.get(uploadPath);
            Files.createDirectories(path);

            for (MultipartFile file : images) {
                String uuidFileName = UUID.randomUUID().toString();
                String originFileName = file.getOriginalFilename();

                if (originFileName == null) {
                    continue;
                }

                String extension = originFileName.substring(originFileName.lastIndexOf(".")).toLowerCase();

                if (!extension.matches("\\.(jpg|jpeg|png)")) {
                    throw new IllegalArgumentException("Invalid image format. Only JPG, JPEG, and PNG formats are allowed");
                }

                String resultFileName = uuidFileName + extension;
                Path filePath = Paths.get(uploadPath, resultFileName);
                file.transferTo(Files.createFile(filePath));

                links.add(resultFileName);
            }

            return links;

        } catch (Exception e) {
            throw new FileManagerException("An error occurred while saving files");
        }
    }

    public void deleteFiles(List<String> imageLinks) throws FileManagerException {
        if (imageLinks == null) {
            return;
        }

        try {
            for (String link : imageLinks) {
                Path filePath = Paths.get(uploadPath, link);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
        } catch (Exception e) {
            throw new FileManagerException("An error occurred while deleting files");
        }
    }
}
