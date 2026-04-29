package com.smartschedule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "schedule_tag_relation")
@IdClass(ScheduleTagRelation.ScheduleTagRelationId.class)
public class ScheduleTagRelation {

    @Id
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Id
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Data
    public static class ScheduleTagRelationId implements Serializable {
        private Long scheduleId;
        private Long tagId;
    }
}