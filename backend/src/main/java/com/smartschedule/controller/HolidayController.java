package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.common.HolidayInfo;
import com.smartschedule.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ApiResponse<List<HolidayInfo>> list(@RequestParam(defaultValue = "2026") int year) {
        return ApiResponse.success(holidayService.getHolidays(year));
    }
}