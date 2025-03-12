package com.tacticboard.core.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {

    String storeFile(MultipartFile file, String directory) throws IOException;

    byte[] getFile(String filePath);

    void deleteFile(String filePath);

    List<String> listFiles(String directory);

    String getFileUrl(String filePath);

    long getStorageUsage(Long userId);
}