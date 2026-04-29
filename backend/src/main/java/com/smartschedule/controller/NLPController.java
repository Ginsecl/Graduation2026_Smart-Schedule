package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.dto.ConflictCheckResult;
import com.smartschedule.dto.NlpParseConflictDTO;
import com.smartschedule.dto.NlpParseRequest;
import com.smartschedule.dto.NlpParseResult;
import com.smartschedule.dto.ScheduleDTO;
import com.smartschedule.nlp.NLPParserService;
import com.smartschedule.service.ConflictDetectionService;
import com.smartschedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nlp")
@RequiredArgsConstructor
public class NLPController {

    private final NLPParserService nlpParserService;
    private final ScheduleService scheduleService;
    private final ConflictDetectionService conflictDetectionService;

    @PostMapping("/parse")
    public ApiResponse<NlpParseResult> parse(@RequestBody NlpParseRequest request) {
        return ApiResponse.success(nlpParserService.parse(request));
    }

    @PostMapping("/parse-and-check")
    public ApiResponse<NlpParseConflictDTO> parseAndCheck(@RequestBody NlpParseRequest request) {
        NlpParseResult parsed = nlpParserService.parse(request);

        ConflictCheckResult conflictCheck = conflictDetectionService.check(
                request.getUserId(),
                parsed.getStartTime(),
                parsed.getEndTime(),
                null
        );

        return ApiResponse.success(new NlpParseConflictDTO(parsed, conflictCheck));
    }

    @PostMapping("/parse-and-create")
    public ApiResponse<ScheduleDTO> parseAndCreate(Authentication authentication,
                                                    @RequestBody NlpParseRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        NlpParseResult parsed = nlpParserService.parse(request);

        ScheduleDTO dto = new ScheduleDTO();
        dto.setTitle(parsed.getTitle());
        dto.setDescription(parsed.getDescription());
        dto.setStartTime(parsed.getStartTime());
        dto.setEndTime(parsed.getEndTime());
        dto.setType(parsed.getType());
        dto.setLocation(parsed.getLocation());
        dto.setParticipants(parsed.getParticipants() != null
                ? toJsonArray(parsed.getParticipants()) : null);
        dto.setImportance(parsed.getImportance() != null ? parsed.getImportance() : 3);
        dto.setSource("NLP");
        dto.setRawText(request.getText());

        return ApiResponse.success(scheduleService.create(userId, dto));
    }

    private String toJsonArray(java.util.List<String> list) {
        return "[" + String.join(",", list.stream()
                .map(s -> "\"" + s + "\"").toList()) + "]";
    }
}