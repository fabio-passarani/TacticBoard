package com.tacticboard.core.util;

import java.util.regex.Pattern;

public final class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,50}$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    
    private ValidationUtils() {
        throw new AssertionError("ValidationUtils class should not be instantiated");
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isValidImageFile(String fileName) {
        if (isNullOrEmpty(fileName)) {
            return false;
        }
        
        String extension = getFileExtension(fileName).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif");
    }
    
    public static boolean isValidVideoFile(String fileName) {
        if (isNullOrEmpty(fileName)) {
            return false;
        }
        
        String extension = getFileExtension(fileName).toLowerCase();
        return extension.equals("mp4") || extension.equals("mov") || 
               extension.equals("avi") || extension.equals("wmv") || 
               extension.equals("mkv");
    }
    
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }
    
    public static String sanitizeFileName(String fileName) {
        if (isNullOrEmpty(fileName)) {
            return "";
        }
        
        // Replace any character that is not a-z, A-Z, 0-9, dot, underscore, or hyphen with underscore
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}