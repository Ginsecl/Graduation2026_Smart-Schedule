package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.dto.ConflictCheckResult;
import com.smartschedule.dto.ScheduleDTO;
import com.smartschedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ApiResponse<ScheduleDTO> create(Authentication authentication, @RequestBody ScheduleDTO dto) {
        Long userId = getUserId(authentication);
        return ApiResponse.success(scheduleService.create(userId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<ScheduleDTO> update(Authentication authentication,
                                            @PathVariable Long id,
                                            @RequestBody ScheduleDTO dto) {
        Long userId = getUserId(authentication);
        return ApiResponse.success(scheduleService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
        Long userId = getUserId(authentication);
        scheduleService.delete(userId, id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleDTO> getById(Authentication authentication, @PathVariable Long id) {
        Long userId = getUserId(authentication);
        return ApiResponse.success(scheduleService.getById(userId, id));
    }

    @GetMapping
    public ApiResponse<List<ScheduleDTO>> list(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        Long userId = getUserId(authentication);
        List<ScheduleDTO> result;
        if (type != null || status != null) {
            result = scheduleService.listByConditions(userId, start, end, type, status);
        } else {
            result = scheduleService.listByDateRange(userId, start, end);
        }
        return ApiResponse.success(result);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ScheduleDTO> updateStatus(Authentication authentication,
                                                  @PathVariable Long id,
                                                  @RequestParam String status) {
        Long userId = getUserId(authentication);
        return ApiResponse.success(scheduleService.updateStatus(userId, id, status));
    }

    @GetMapping("/check-conflict")
    public ApiResponse<ConflictCheckResult> checkConflict(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Long excludeScheduleId) {
        Long userId = getUserId(authentication);
        return ApiResponse.success(scheduleService.checkConflict(userId, startTime, endTime, excludeScheduleId));
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}