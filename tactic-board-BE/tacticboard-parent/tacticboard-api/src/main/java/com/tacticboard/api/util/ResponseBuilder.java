package com.tacticboard.api.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {

    private ResponseBuilder() {
        // Utility class, no instantiation
    }

    public static ResponseEntity<?> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<?> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<?> created(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CREATED.value());
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static ResponseEntity<?> error(HttpStatus status, String message, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("success", false);
        response.put("message", message);
        response.put("details", details);
        return ResponseEntity.status(status).body(response);
    }
}