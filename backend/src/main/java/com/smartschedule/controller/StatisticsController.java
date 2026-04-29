package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(statisticsService.getOverview(userId));
    }

    @GetMapping("/type-distribution")
    public ApiResponse<Map<String, Object>> typeDistribution(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(statisticsService.getTypeDistribution(userId));
    }

    @GetMapping("/weekly")
    public ApiResponse<Map<String, Object>> weekly(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(statisticsService.getWeeklyStats(userId));
    }

    @GetMapping("/monthly")
    public ApiResponse<Map<String, Object>> monthly(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(statisticsService.getMonthlyStats(userId));
    }
}