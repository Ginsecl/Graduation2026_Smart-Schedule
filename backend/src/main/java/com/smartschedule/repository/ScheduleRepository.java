package com.smartschedule.repository;

import com.smartschedule.common.ScheduleStatus;
import com.smartschedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUserId(Long userId);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.userId = :userId AND s.important = true")
    long countImportantByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.userId = :userId AND s.important = true AND s.status = :status")
    long countImportantByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ScheduleStatus status);

    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId " +
           "AND s.startTime >= :start AND s.startTime <= :end " +
           "ORDER BY s.startTime ASC")
    List<Schedule> findByUserIdAndStartTimeBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId " +
           "AND s.status <> 'CANCELLED' " +
           "AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Schedule> findOverlapping(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId " +
           "AND s.startTime >= :start AND s.startTime <= :end " +
           "AND (:type IS NULL OR s.type = :type) " +
           "AND (:status IS NULL OR s.status = :status) " +
           "ORDER BY s.startTime ASC")
    List<Schedule> findByConditions(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("type") String type,
            @Param("status") String status);

    long countByUserIdAndStatus(Long userId, ScheduleStatus status);

    @Query("SELECT s.type, COUNT(s) FROM Schedule s WHERE s.userId = :userId GROUP BY s.type")
    List<Object[]> countByTypeGrouped(@Param("userId") Long userId);
}