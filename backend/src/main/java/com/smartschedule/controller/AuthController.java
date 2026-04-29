package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.dto.*;
import com.smartschedule.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.success(authService.refreshToken(request));
    }

    @GetMapping("/profile")
    public ApiResponse<UserDTO> profile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(authService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(Authentication authentication,
                                                @RequestBody UserDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(authService.updateProfile(userId, dto));
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(Authentication authentication,
                                             @RequestBody Map<String, String> body) {
        Long userId = (Long) authentication.getPrincipal();
        authService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return ApiResponse.success(null);
    }
}