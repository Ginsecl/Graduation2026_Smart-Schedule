package com.smartschedule.dto;

import com.smartschedule.common.OverlapType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ConflictCheckResult {
    private boolean hasConflict;
    private List<Conflict> conflicts;
    private List<ResolutionSuggestion> suggestions;

    @Data
    @AllArgsConstructor
    public static class Conflict {
        private Long scheduleId;
        private String title;
        private OverlapType overlapType;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String type;
        private int importance;
    }

    @Data
    @AllArgsConstructor
    public static class ResolutionSuggestion {
        private String strategy;
        private String label;
        private String description;
        private LocalDateTime adjustedStartTime;
        private LocalDateTime adjustedEndTime;
        private Integer adjustedDurationMinutes;
    }
}