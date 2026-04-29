package com.smartschedule.dto;

import lombok.Data;

@Data
public class NlpParseRequest {
    private String text;
    private Long userId;
}