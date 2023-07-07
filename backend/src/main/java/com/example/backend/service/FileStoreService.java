package com.example.backend.service;

import com.example.backend.exception.FileManagerException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStoreService {
    List<String> saveFiles(List<MultipartFile> files) throws IllegalArgumentException, FileManagerException;
    void deleteFiles(List<String> links) throws FileManagerException;
}
