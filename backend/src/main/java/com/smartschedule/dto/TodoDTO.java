package com.smartschedule.dto;

import com.smartschedule.entity.Todo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoDTO {
    private Long id;
    private Long userId;
    private String title;
    private Boolean important;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TodoDTO fromTodo(Todo t) {
        TodoDTO dto = new TodoDTO();
        dto.setId(t.getId());
        dto.setUserId(t.getUserId());
        dto.setTitle(t.getTitle());
        dto.setImportant(t.getImportant());
        dto.setCompleted(t.getCompleted());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        return dto;
    }
}
