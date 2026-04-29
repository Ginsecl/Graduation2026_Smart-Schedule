package com.smartschedule.dto;

import com.smartschedule.entity.ScheduleTag;
import lombok.Data;

@Data
public class ScheduleTagDTO {
    private Long id;
    private Long userId;
    private String name;
    private String color;
    private String icon;

    public static ScheduleTagDTO fromTag(ScheduleTag tag) {
        ScheduleTagDTO dto = new ScheduleTagDTO();
        dto.setId(tag.getId());
        dto.setUserId(tag.getUserId());
        dto.setName(tag.getName());
        dto.setColor(tag.getColor());
        dto.setIcon(tag.getIcon());
        return dto;
    }
}