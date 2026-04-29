package com.smartschedule.service;

import com.smartschedule.common.ScheduleStatus;
import com.smartschedule.common.ScheduleType;
import com.smartschedule.dto.ConflictCheckResult;
import com.smartschedule.dto.ScheduleDTO;
import com.smartschedule.entity.Schedule;
import com.smartschedule.repository.ScheduleRepository;
import com.smartschedule.repository.ScheduleTagRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleTagRelationRepository tagRelationRepository;
    private final ConflictDetectionService conflictDetectionService;

    @Transactional
    public ScheduleDTO create(Long userId, ScheduleDTO dto) {
        Schedule schedule = new Schedule();
        buildSchedule(schedule, userId, dto);
        schedule = scheduleRepository.save(schedule);
        return ScheduleDTO.fromSchedule(schedule);
    }

    @Transactional
    public ScheduleDTO update(Long userId, Long scheduleId, ScheduleDTO dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("日程不存在"));
        if (!schedule.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此日程");
        }
        buildSchedule(schedule, userId, dto);
        schedule = scheduleRepository.save(schedule);
        return ScheduleDTO.fromSchedule(schedule);
    }

    @Transactional
    public void delete(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("日程不存在"));
        if (!schedule.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此日程");
        }
        tagRelationRepository.deleteByScheduleId(scheduleId);
        scheduleRepository.delete(schedule);
    }

    public ScheduleDTO getById(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("日程不存在"));
        if (!schedule.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此日程");
        }
        return ScheduleDTO.fromSchedule(schedule);
    }

    public List<ScheduleDTO> listByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByUserIdAndStartTimeBetween(userId, start, end)
                .stream().map(ScheduleDTO::fromSchedule).collect(Collectors.toList());
    }

    public List<ScheduleDTO> listByConditions(Long userId, LocalDateTime start, LocalDateTime end,
                                               String type, String status) {
        return scheduleRepository.findByConditions(userId, start, end, type, status)
                .stream().map(ScheduleDTO::fromSchedule).collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDTO updateStatus(Long userId, Long scheduleId, String status) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("日程不存在"));
        if (!schedule.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此日程");
        }
        schedule.setStatus(ScheduleStatus.valueOf(status.toUpperCase()));
        schedule = scheduleRepository.save(schedule);
        return ScheduleDTO.fromSchedule(schedule);
    }

    public ConflictCheckResult checkConflict(Long userId, LocalDateTime startTime, LocalDateTime endTime,
                                              Long excludeScheduleId) {
        return conflictDetectionService.check(userId, startTime, endTime, excludeScheduleId);
    }

    private void buildSchedule(Schedule schedule, Long userId, ScheduleDTO dto) {
        schedule.setUserId(userId);
        if (dto.getTitle() != null) schedule.setTitle(dto.getTitle());
        if (dto.getDescription() != null) schedule.setDescription(dto.getDescription());
        if (dto.getStartTime() != null) schedule.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) schedule.setEndTime(dto.getEndTime());
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            schedule.setDurationMinutes((int) Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes());
        }
        if (dto.getType() != null) schedule.setType(ScheduleType.valueOf(dto.getType().toUpperCase()));
        if (dto.getStatus() != null) schedule.setStatus(ScheduleStatus.valueOf(dto.getStatus().toUpperCase()));
        if (dto.getImportance() != null) schedule.setImportance(dto.getImportance());
        if (dto.getImportant() != null) schedule.setImportant(dto.getImportant());
        if (dto.getLocation() != null) schedule.setLocation(dto.getLocation());
        if (dto.getParticipants() != null) schedule.setParticipants(dto.getParticipants());
        if (dto.getRepeatRule() != null) schedule.setRepeatRule(dto.getRepeatRule());
        if (dto.getSource() != null) schedule.setSource(dto.getSource());
        if (dto.getRawText() != null) schedule.setRawText(dto.getRawText());
        if (dto.getExtensions() != null) schedule.setExtensions(dto.getExtensions());
    }
}