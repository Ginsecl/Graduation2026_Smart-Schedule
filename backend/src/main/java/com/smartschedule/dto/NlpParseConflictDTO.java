package com.smartschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NlpParseConflictDTO {
    private NlpParseResult parsedSchedule;
    private ConflictCheckResult conflictCheck;
}