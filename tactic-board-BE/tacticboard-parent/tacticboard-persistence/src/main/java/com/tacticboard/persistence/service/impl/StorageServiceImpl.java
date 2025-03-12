package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.BusinessLogicException;
import com.tacticboard.core.service.StorageService;
import com.tacticboard.core.util.Constants;
import com.tacticboard.core.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl implements StorageService {

    private final String storageLocation;
    private final String bucketName;
    private final S3Client s3Client;
    private final boolean useS3;

    public StorageServiceImpl(
            @Value("${app.storage.location:uploads}") String storageLocation,
            @Value("${app.storage.s3.bucket:}") String bucketName,
            @Value("${app.storage.use-s3:false}") boolean useS3) {
        
        this.storageLocation = storageLocation;
        this.bucketName = bucketName;
        this.useS3 = useS3;
        
        if (useS3) {
            this.s3Client = S3Client.builder()
                    .region(Region.US_EAST_1) // Configure as needed
                    .build();
        } else {
            this.s3Client = null;
            // Create local storage directory if it doesn't exist
            try {
                Files.createDirectories(Paths.get(storageLocation));
            } catch (IOException e) {
                throw new BusinessLogicException("Could not create storage directory", e);
            }
        }
    }

    @Override
    public String storeFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessLogicException("Failed to store empty file");
        }
        
        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = ValidationUtils.sanitizeFileName(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "_" + sanitizedFilename;
        String filePath = directory + "/" + uniqueFilename;
        
        if (useS3) {
            // Store in S3
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } else {
            // Store locally
            Path targetLocation = Paths.get(storageLocation).resolve(filePath);
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation);
        }
        
        return filePath;
    }

    @Override
    public byte[] getFile(String filePath) {
        try {
            if (useS3) {
                // Get from S3
                GetObjectRequest request = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filePath)
                        .build();
                
                return s3Client.getObjectAsBytes(request).asByteArray();
            } else {
                // Get from local storage
                Path targetLocation = Paths.get(storageLocation).resolve(filePath);
                return Files.readAllBytes(targetLocation);
            }
        } catch (IOException | S3Exception e) {
            throw new BusinessLogicException("Failed to read file: " + filePath, e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            if (useS3) {
                // Delete from S3
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filePath)
                        .build();
                
                s3Client.deleteObject(request);
            } else {
                // Delete from local storage
                Path targetLocation = Paths.get(storageLocation).resolve(filePath);
                Files.deleteIfExists(targetLocation);
            }
        } catch (IOException | S3Exception e) {
            throw new BusinessLogicException("Failed to delete file: " + filePath, e);
        }
    }

    @Override
    public List<String> listFiles(String directory) {
        try {
            if (useS3) {
                // List files in S3
                ListObjectsV2Request request = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(directory)
                        .build();
                
                ListObjectsV2Response response = s3Client.listObjectsV2(request);
                return response.contents().stream()
                        .map(S3Object::key)
                        .collect(Collectors.toList());
            } else {
                // List files in local storage
                Path targetLocation = Paths.get(storageLocation).resolve(directory);
                if (!Files.exists(targetLocation)) {
                    return new ArrayList<>();
                }
                
                return Files.list(targetLocation)
                        .map(path -> directory + "/" + path.getFileName().toString())
                        .collect(Collectors.toList());
            }
        } catch (IOException | S3Exception e) {
            throw new BusinessLogicException("Failed to list files in directory: " + directory, e);
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        if (useS3) {
            // Generate S3 URL
            return "https://" + bucketName + ".s3.amazonaws.com/" + filePath;
        } else {
            // Generate local URL
            return "/api/v1/files/" + filePath;
        }
    }

    @Override
    public long getStorageUsage(Long userId) {
        long totalSize = 0;
        
        // Calculate size for each directory type
        totalSize += calculateDirectorySize(Constants.PROFILE_PICTURES_DIR + "/" + userId);
        totalSize += calculateDirectorySize(Constants.TEAM_LOGOS_DIR + "/" + userId);
        totalSize += calculateDirectorySize(Constants.PLAYER_PHOTOS_DIR + "/" + userId);
        totalSize += calculateDirectorySize(Constants.VIDEOS_DIR + "/" + userId);
        totalSize += calculateDirectorySize(Constants.EXERCISE_DIAGRAMS_DIR + "/" + userId);
        totalSize += calculateDirectorySize(Constants.TACTIC_THUMBNAILS_DIR + "/" + userId);
        
        return totalSize;
    }
    
    private long calculateDirectorySize(String directory) {
        try {
            if (useS3) {
                // Calculate size in S3
                ListObjectsV2Request request = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(directory)
                        .build();
                
                ListObjectsV2Response response = s3Client.listObjectsV2(request);
                return response.contents().stream()
                        .mapToLong(S3Object::size)
                        .sum();
            } else {
                // Calculate size in local storage
                Path targetLocation = Paths.get(storageLocation).resolve(directory);
                if (!Files.exists(targetLocation)) {
                    return 0;
                }
                
                return Files.walk(targetLocation)
                        .filter(Files::isRegularFile)
                        .mapToLong(path -> {
                            try {
                                return Files.size(path);
                            } catch (IOException e) {
                                return 0;
                            }
                        })
                        .sum();
            }
        } catch (IOException | S3Exception e) {
            throw new BusinessLogicException("Failed to calculate directory size: " + directory, e);
        }
    }
}