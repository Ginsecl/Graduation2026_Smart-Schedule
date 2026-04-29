package com.smartschedule.service;

import com.smartschedule.common.ScheduleStatus;
import com.smartschedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ScheduleRepository scheduleRepository;

    public Map<String, Object> getOverview(Long userId) {
        long total = scheduleRepository.countByUserId(userId);
        long completed = scheduleRepository.countByUserIdAndStatus(userId, ScheduleStatus.COMPLETED);
        long importantCount = scheduleRepository.countImportantByUserId(userId);
        long importantCompleted = scheduleRepository.countImportantByUserIdAndStatus(userId, ScheduleStatus.COMPLETED);

        LocalDateTime now = LocalDateTime.now();
        long todayCount = scheduleRepository.findByUserIdAndStartTimeBetween(
                userId, now.toLocalDate().atStartOfDay(), now.toLocalDate().plusDays(1).atStartOfDay()).size();

        return Map.of(
                "total", total,
                "completed", completed,
                "importantCount", importantCount,
                "importantCompleted", importantCompleted,
                "todayCount", todayCount,
                "completionRate", total > 0 ? (double) completed / total : 0
        );
    }

    public Map<String, Object> getTypeDistribution(Long userId) {
        List<Object[]> rows = scheduleRepository.countByTypeGrouped(userId);
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (Object[] row : rows) {
            distribution.put(row[0].toString(), (Long) row[1]);
        }
        return Map.of("distribution", distribution);
    }

    public Map<String, Object> getWeeklyStats(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusDays(7);

        List<com.smartschedule.entity.Schedule> weekSchedules = scheduleRepository
                .findByUserIdAndStartTimeBetween(userId, weekStart, weekEnd);

        Map<String, Long> dailyCount = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 0; i < 7; i++) {
            LocalDateTime day = weekStart.plusDays(i);
            String key = day.format(fmt);
            long count = weekSchedules.stream()
                    .filter(s -> s.getStartTime().toLocalDate().equals(day.toLocalDate()))
                    .count();
            dailyCount.put(key, count);
        }

        return Map.of(
                "weekStart", weekStart.toString(),
                "weekEnd", weekEnd.toString(),
                "dailyCount", dailyCount,
                "totalThisWeek", weekSchedules.size()
        );
    }

    public Map<String, Object> getMonthlyStats(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        List<com.smartschedule.entity.Schedule> monthSchedules = scheduleRepository
                .findByUserIdAndStartTimeBetween(userId, monthStart, monthEnd);

        long completed = monthSchedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.COMPLETED).count();

        return Map.of(
                "monthStart", monthStart.toString(),
                "monthEnd", monthEnd.toString(),
                "totalThisMonth", monthSchedules.size(),
                "completedThisMonth", completed
        );
    }
}