package com.smartschedule.controller;

import com.smartschedule.common.ApiResponse;
import com.smartschedule.dto.TodoDTO;
import com.smartschedule.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ApiResponse<TodoDTO> create(Authentication authentication, @RequestBody TodoDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(todoService.create(userId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<TodoDTO> update(Authentication authentication,
                                        @PathVariable("id") Long id,
                                        @RequestBody TodoDTO dto) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(todoService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable("id") Long id) {
        Long userId = (Long) authentication.getPrincipal();
        todoService.delete(userId, id);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<TodoDTO>> list(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(todoService.list(userId));
    }

    @PatchMapping("/{id}/toggle")
    public ApiResponse<TodoDTO> toggleComplete(Authentication authentication, @PathVariable("id") Long id) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(todoService.toggleComplete(userId, id));
    }
}
