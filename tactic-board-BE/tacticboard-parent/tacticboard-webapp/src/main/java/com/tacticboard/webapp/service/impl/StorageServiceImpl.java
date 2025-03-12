package com.tacticboard.webapp.service.impl;

import com.tacticboard.core.service.StorageService;
import com.tacticboard.webapp.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;

    public StorageServiceImpl(@Value("${app.storage.location:${user.home}/tacticboard/uploads}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file");
        }
        
        Path directoryPath = this.rootLocation.resolve(directory);
        Files.createDirectories(directoryPath);
        
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtils.getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        Path destinationFile = directoryPath.resolve(filename)
                .normalize().toAbsolutePath();
        
        if (!destinationFile.getParent().equals(directoryPath.toAbsolutePath())) {
            throw new SecurityException("Cannot store file outside current directory");
        }
        
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        
        return directory + "/" + filename;
    }

    @Override
    public byte[] getFile(String filePath) {
        try {
            Path file = rootLocation.resolve(filePath);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            log.error("Failed to read file: {}", filePath, e);
            throw new RuntimeException("Failed to read file", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path file = rootLocation.resolve(filePath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public List<String> listFiles(String directory) {
        try {
            Path directoryPath = rootLocation.resolve(directory);
            return Files.list(directoryPath)
                    .filter(Files::isRegularFile)
                    .map(path -> directory + "/" + path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list files in directory: {}", directory, e);
            throw new RuntimeException("Failed to list files", e);
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return "/api/files/" + filePath;
    }

    @Override
    public long getStorageUsage(Long userId) {
        try {
            Path userDirectory = rootLocation.resolve("user-" + userId);
            if (!Files.exists(userDirectory)) {
                return 0;
            }
            
            return Files.walk(userDirectory)
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            log.error("Failed to calculate storage usage for user: {}", userId, e);
            return 0;
        }
    }
}