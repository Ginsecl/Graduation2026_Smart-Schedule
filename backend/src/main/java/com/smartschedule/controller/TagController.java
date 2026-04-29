package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.dto.ScheduleTagDTO;
import com.smartschedule.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ApiResponse<ScheduleTagDTO> create(Authentication authentication, @RequestBody ScheduleTagDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(tagService.create(userId, dto));
    }

    @GetMapping
    public ApiResponse<List<ScheduleTagDTO>> list(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(tagService.listByUser(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<ScheduleTagDTO> update(Authentication authentication,
                                               @PathVariable Long id,
                                               @RequestBody ScheduleTagDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(tagService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        tagService.delete(userId, id);
        return ApiResponse.success(null);
    }
}