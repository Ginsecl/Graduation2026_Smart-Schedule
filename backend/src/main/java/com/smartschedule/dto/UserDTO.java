package com.smartschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String timezone;
    private String preferences;
    private LocalDateTime createdAt;

    public static UserDTO fromUser(com.smartschedule.entity.User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setTimezone(user.getTimezone());
        dto.setPreferences(user.getPreferences());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}