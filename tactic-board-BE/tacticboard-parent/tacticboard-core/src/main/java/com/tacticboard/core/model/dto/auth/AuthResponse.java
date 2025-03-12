package com.tacticboard.core.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String message;
    
    public AuthResponse(String token) {
        this.token = token;
        this.message = "Authentication successful";
    }
}