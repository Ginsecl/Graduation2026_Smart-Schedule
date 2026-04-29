package com.smartschedule.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NlpParseResult {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String type;
    private String location;
    private List<String> participants;
    private Integer importance;
    private Double confidence;
    private String rawText;
    private String intent;
    private boolean needsConfirmation;

    @Data
    public static class Alternative {
        private String title;
        private Double confidence;
    }
}