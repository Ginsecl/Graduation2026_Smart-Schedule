package com.smartschedule.dto;

import com.smartschedule.entity.Schedule;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String type;
    private String status;
    private Integer importance;
    private Boolean important;
    private String location;
    private String participants;
    private String repeatRule;
    private String source;
    private String rawText;
    private String extensions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScheduleDTO fromSchedule(Schedule s) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(s.getId());
        dto.setUserId(s.getUserId());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setDurationMinutes(s.getDurationMinutes());
        dto.setType(s.getType() != null ? s.getType().name() : null);
        dto.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
        dto.setImportance(s.getImportance());
        dto.setImportant(s.getImportant());
        dto.setLocation(s.getLocation());
        dto.setParticipants(s.getParticipants());
        dto.setRepeatRule(s.getRepeatRule());
        dto.setSource(s.getSource());
        dto.setRawText(s.getRawText());
        dto.setExtensions(s.getExtensions());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setUpdatedAt(s.getUpdatedAt());
        return dto;
    }
}