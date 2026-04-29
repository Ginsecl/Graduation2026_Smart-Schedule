package com.smartschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserDTO user;

    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn, UserDTO user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}