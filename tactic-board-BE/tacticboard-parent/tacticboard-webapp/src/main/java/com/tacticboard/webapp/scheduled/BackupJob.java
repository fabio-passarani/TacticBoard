package com.tacticboard.webapp.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BackupJob {

    private final DataSource dataSource;
    
    @Value("${app.backup.enabled:false}")
    private boolean backupEnabled;
    
    @Value("${app.backup.location:${user.home}/tacticboard/backups}")
    private String backupLocation;
    
    @Value("${app.backup.keep-count:10}")
    private int keepCount;

    @Autowired
    public BackupJob(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(cron = "${app.backup.cron:0 0 2 * * ?}") // Default: every day at 2 AM
    public void performBackup() {
        if (!backupEnabled) {
            log.info("Database backup is disabled");
            return;
        }
        
        log.info("Starting database backup...");
        
        try {
            // Create backup directory if it doesn't exist
            Path backupDir = Paths.get(backupLocation);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            // Generate backup filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFilename = "tacticboard_backup_" + timestamp + ".sql";
            Path backupFile = backupDir.resolve(backupFilename);
            
            // Execute backup command (this is a simplified example)
            // In a real implementation, you would use a proper database backup tool
            // or Spring's JdbcTemplate to execute a backup procedure
            
            // For H2 database (example)
            if (isDatabaseH2()) {
                executeH2Backup(backupFile.toString());
            } 
            // For MySQL database (example)
            else if (isDatabaseMySQL()) {
                executeMySQLBackup(backupFile.toString());
            }
            // For PostgreSQL database (example)
            else if (isDatabasePostgreSQL()) {
                executePostgreSQLBackup(backupFile.toString());
            }
            else {
                log.warn("Backup not implemented for the current database type");
                return;
            }
            
            log.info("Database backup completed successfully: {}", backupFile);
            
            // Clean up old backups (keep last N)
            cleanupOldBackups(backupDir);
            
        } catch (Exception e) {
            log.error("Failed to perform database backup", e);
        }
    }
    
    private boolean isDatabaseH2() {
        try {
            return dataSource.getConnection().getMetaData().getURL().contains("h2");
        } catch (Exception e) {
            log.error("Error checking database type", e);
            return false;
        }
    }
    
    private boolean isDatabaseMySQL() {
        try {
            return dataSource.getConnection().getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
        } catch (Exception e) {
            log.error("Error checking database type", e);
            return false;
        }
    }
    
    private boolean isDatabasePostgreSQL() {
        try {
            return dataSource.getConnection().getMetaData().getDatabaseProductName().toLowerCase().contains("postgresql");
        } catch (Exception e) {
            log.error("Error checking database type", e);
            return false;
        }
    }
    
    private void executeH2Backup(String backupFile) throws IOException {
        // H2 specific backup logic
        log.info("Executing H2 database backup to {}", backupFile);
        // Example: SCRIPT command for H2
        // This is a placeholder - actual implementation would use JDBC to execute SCRIPT command
    }
    
    private void executeMySQLBackup(String backupFile) throws IOException {
        // MySQL specific backup logic
        log.info("Executing MySQL database backup to {}", backupFile);
        // Example: Use ProcessBuilder to execute mysqldump
        // This is a placeholder - actual implementation would use ProcessBuilder
    }
    
    private void executePostgreSQLBackup(String backupFile) throws IOException {
        // PostgreSQL specific backup logic
        log.info("Executing PostgreSQL database backup to {}", backupFile);
        // Example: Use ProcessBuilder to execute pg_dump
        // This is a placeholder - actual implementation would use ProcessBuilder
    }
    
    private void cleanupOldBackups(Path backupDir) throws IOException {
        log.info("Cleaning up old backups, keeping {} most recent", keepCount);
        
        List<Path> backupFiles = Files.list(backupDir)
                .filter(path -> path.toString().endsWith(".sql"))
                .sorted(Comparator.comparing(path -> {
                    try {
                        return Files.getLastModifiedTime(path);
                    } catch (IOException e) {
                        return null;
                    }
                }).reversed())
                .collect(Collectors.toList());
        
        if (backupFiles.size() > keepCount) {
            for (int i = keepCount; i < backupFiles.size(); i++) {
                Path fileToDelete = backupFiles.get(i);
                log.info("Deleting old backup: {}", fileToDelete);
                Files.deleteIfExists(fileToDelete);
            }
        }
    }
}