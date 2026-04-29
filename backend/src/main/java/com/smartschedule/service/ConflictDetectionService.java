package com.smartschedule.service;

import com.smartschedule.common.OverlapType;
import com.smartschedule.common.ScheduleStatus;
import com.smartschedule.dto.ConflictCheckResult;
import com.smartschedule.dto.ConflictCheckResult.Conflict;
import com.smartschedule.dto.ConflictCheckResult.ResolutionSuggestion;
import com.smartschedule.entity.Schedule;
import com.smartschedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConflictDetectionService {

    private final ScheduleRepository scheduleRepository;

    public ConflictCheckResult check(Long userId, LocalDateTime newStart, LocalDateTime newEnd,
                                      Long excludeScheduleId) {
        List<Schedule> overlapping = scheduleRepository.findOverlapping(userId, newStart, newEnd);

        List<Conflict> conflicts = new ArrayList<>();
        for (Schedule s : overlapping) {
            if (excludeScheduleId != null && s.getId().equals(excludeScheduleId)) {
                continue;
            }
            OverlapType overlapType = determineOverlapType(newStart, newEnd, s.getStartTime(), s.getEndTime());
            conflicts.add(new Conflict(
                    s.getId(),
                    s.getTitle(),
                    overlapType,
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getType() != null ? s.getType().name() : null,
                    s.getImportance()
            ));
        }

        List<ResolutionSuggestion> suggestions = new ArrayList<>();
        if (!conflicts.isEmpty()) {
            suggestions = generateResolutions(userId, newStart, newEnd, conflicts);
        }

        return new ConflictCheckResult(!conflicts.isEmpty(), conflicts, suggestions);
    }

    private OverlapType determineOverlapType(LocalDateTime newStart, LocalDateTime newEnd,
                                              LocalDateTime existStart, LocalDateTime existEnd) {
        if (!newStart.isBefore(existStart) && !newEnd.isAfter(existEnd)) {
            return OverlapType.CONTAINED;
        }
        if (!existStart.isBefore(newStart) && !existEnd.isAfter(newEnd)) {
            return OverlapType.CONTAINING;
        }
        return OverlapType.PARTIAL;
    }

    private List<ResolutionSuggestion> generateResolutions(Long userId, LocalDateTime newStart,
                                                            LocalDateTime newEnd, List<Conflict> conflicts) {
        LocalDateTime conflictEnd = conflicts.stream()
                .map(Conflict::getEndTime)
                .max(Comparator.naturalOrder())
                .orElse(newEnd);
        LocalDateTime conflictStart = conflicts.stream()
                .map(Conflict::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(newStart);

        long newDurationMinutes = Duration.between(newStart, newEnd).toMinutes();
        List<ResolutionSuggestion> suggestions = new ArrayList<>();

        suggestions.add(buildPostpone(newStart, newEnd, conflictEnd));

        suggestions.add(buildAdvanceEnd(newStart, newEnd, conflictStart));

        suggestions.add(buildShorten(newStart, conflictStart, newDurationMinutes));

        findFreeSlot(userId, newStart.toLocalDate(), newDurationMinutes).ifPresent(
                slot -> suggestions.add(slot));

        return suggestions;
    }

    private ResolutionSuggestion buildPostpone(LocalDateTime newStart, LocalDateTime newEnd,
                                                LocalDateTime conflictEnd) {
        long duration = Duration.between(newStart, newEnd).toMinutes();
        LocalDateTime adjustedStart = conflictEnd;
        LocalDateTime adjustedEnd = adjustedStart.plusMinutes(duration);
        return new ResolutionSuggestion(
                "POSTPONE",
                "推迟开始时间",
                "保持时长不变，开始时间推迟至 " + formatTime(adjustedStart) + "，" + duration + " 分钟",
                adjustedStart,
                adjustedEnd,
                (int) duration
        );
    }

    private ResolutionSuggestion buildAdvanceEnd(LocalDateTime newStart, LocalDateTime newEnd,
                                                  LocalDateTime conflictStart) {
        LocalDateTime adjustedEnd = conflictStart;
        long duration = Duration.between(newStart, adjustedEnd).toMinutes();
        if (duration <= 0) {
            return new ResolutionSuggestion(
                    "ADVANCE_END",
                    "提前结束",
                    "无法提前结束（冲突从开始时间之前即存在），建议选择其他方案",
                    newStart,
                    newEnd,
                    (int) Duration.between(newStart, newEnd).toMinutes()
            );
        }
        return new ResolutionSuggestion(
                "ADVANCE_END",
                "提前结束时间",
                "缩短日程，结束时间提前至 " + formatTime(adjustedEnd) + "，持续 " + duration + " 分钟",
                newStart,
                adjustedEnd,
                (int) duration
        );
    }

    private ResolutionSuggestion buildShorten(LocalDateTime newStart, LocalDateTime conflictStart,
                                               long originalDuration) {
        long available = Duration.between(newStart, conflictStart).toMinutes();
        long shortened = Math.min(available, originalDuration);
        if (shortened <= 0) {
            return new ResolutionSuggestion(
                    "SHORTEN",
                    "缩短时长",
                    "冲突前无可用时间，建议选择推迟方案",
                    newStart,
                    newStart.plusMinutes(originalDuration),
                    (int) originalDuration
            );
        }
        LocalDateTime adjustedEnd = newStart.plusMinutes(shortened);
        return new ResolutionSuggestion(
                "SHORTEN",
                "缩短日程时长",
                "缩短至冲突前可用时段，持续 " + shortened + " 分钟，结束于 " + formatTime(adjustedEnd),
                newStart,
                adjustedEnd,
                (int) shortened
        );
    }

    private java.util.Optional<ResolutionSuggestion> findFreeSlot(Long userId, java.time.LocalDate date,
                                                                    long minDurationMinutes) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.of(23, 59, 59));

        List<Schedule> daySchedules = scheduleRepository.findByUserIdAndStartTimeBetween(userId, dayStart, dayEnd)
                .stream()
                .filter(s -> s.getStatus() != ScheduleStatus.CANCELLED)
                .sorted(Comparator.comparing(Schedule::getStartTime))
                .collect(Collectors.toList());

        List<LocalDateTime[]> freeSlots = computeFreeSlots(dayStart, dayEnd, daySchedules);

        for (LocalDateTime[] slot : freeSlots) {
            long slotDuration = Duration.between(slot[0], slot[1]).toMinutes();
            if (slotDuration >= minDurationMinutes) {
                LocalDateTime adjustedEnd = slot[0].plusMinutes(minDurationMinutes);
                return java.util.Optional.of(new ResolutionSuggestion(
                        "FREE_SLOT",
                        "推荐空闲时段",
                        "当天 " + formatTime(slot[0]) + " 起有 " + slotDuration + " 分钟空闲",
                        slot[0],
                        adjustedEnd,
                        (int) minDurationMinutes
                ));
            }
        }

        for (LocalDateTime[] slot : freeSlots) {
            long slotDuration = Duration.between(slot[0], slot[1]).toMinutes();
            if (slotDuration >= 60) {
                LocalDateTime adjustedEnd = slot[0].plusMinutes(Math.min(slotDuration, minDurationMinutes));
                return java.util.Optional.of(new ResolutionSuggestion(
                        "FREE_SLOT",
                        "推荐空闲时段",
                        "当天 " + formatTime(slot[0]) + " 起有 " + slotDuration + " 分钟空闲（小于原时长）",
                        slot[0],
                        adjustedEnd,
                        (int) slotDuration
                ));
            }
        }

        return java.util.Optional.empty();
    }

    private List<LocalDateTime[]> computeFreeSlots(LocalDateTime dayStart, LocalDateTime dayEnd,
                                                    List<Schedule> schedules) {
        List<LocalDateTime[]> freeSlots = new ArrayList<>();
        LocalDateTime cursor = dayStart;

        for (Schedule s : schedules) {
            if (s.getStartTime().isAfter(cursor)) {
                freeSlots.add(new LocalDateTime[]{cursor, s.getStartTime()});
            }
            if (s.getEndTime().isAfter(cursor)) {
                cursor = s.getEndTime();
            }
        }

        if (cursor.isBefore(dayEnd)) {
            freeSlots.add(new LocalDateTime[]{cursor, dayEnd});
        }

        return freeSlots;
    }

    private String formatTime(LocalDateTime dateTime) {
        return dateTime.toLocalTime().toString().substring(0, 5);
    }
}