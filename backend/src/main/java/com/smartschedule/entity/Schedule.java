package com.smartschedule.entity;

import com.smartschedule.common.ScheduleStatus;
import com.smartschedule.common.ScheduleType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "schedules")
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScheduleType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Column(columnDefinition = "TINYINT")
    private Integer importance = 3;

    @Column(nullable = false)
    private Boolean important = false;

    @Column(length = 200)
    private String location;

    @Column(columnDefinition = "JSON")
    private String participants;

    @Column(name = "repeat_rule", columnDefinition = "JSON")
    private String repeatRule;

    @Column(length = 20)
    private String source = "MANUAL";

    @Column(name = "raw_text", length = 1000)
    private String rawText;

    @Column(columnDefinition = "JSON")
    private String extensions;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}