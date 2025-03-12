package com.tacticboard.webapp.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Utility class for file operations like upload, download, and management.
 */
@Component
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    
    private final Path fileStorageLocation;

    /**
     * Initializes the file storage location from application properties.
     * 
     * @param storageLocation The configured storage location path
     * @throws RuntimeException if the file storage location cannot be created
     */
    public FileUtils(@Value("${app.storage.location}") String storageLocation) {
        this.fileStorageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        
        logger.info("File storage location initialized at: {}", this.fileStorageLocation);
    }
    
    /**
     * Stores a file with a generated unique filename.
     * 
     * @param file The file to store
     * @return The generated filename
     * @throws RuntimeException if the file cannot be stored
     */
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = FilenameUtils.getExtension(originalFilename);
                if (!fileExtension.isEmpty()) {
                    fileExtension = "." + fileExtension;
                }
            }
            
            // Generate a unique filename
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            logger.debug("File stored successfully: {}", filename);
            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file.", ex);
        }
    }
    
    /**
     * Loads a file as a Resource.
     * 
     * @param filename The name of the file to load
     * @return The file as a Resource
     * @throws RuntimeException if the file cannot be read
     */
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + filename, ex);
        }
    }
    
    /**
     * Deletes a file.
     * 
     * @param filename The name of the file to delete
     * @return true if the file was deleted successfully, false otherwise
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            logger.error("Error deleting file: {}", filename, ex);
            return false;
        }
    }
    
    /**
     * Creates a subdirectory in the storage location.
     * 
     * @param directoryName The name of the directory to create
     * @return The path to the created directory
     * @throws RuntimeException if the directory cannot be created
     */
    public Path createDirectory(String directoryName) {
        try {
            Path directoryPath = this.fileStorageLocation.resolve(directoryName);
            return Files.createDirectories(directoryPath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create directory: " + directoryName, ex);
        }
    }
    
    /**
     * Gets the absolute path of the file storage location.
     * 
     * @return The absolute path of the file storage location
     */
    public String getFileStorageLocation() {
        return this.fileStorageLocation.toString();
    }
}