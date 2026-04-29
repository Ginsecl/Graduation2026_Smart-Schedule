package com.smartschedule.repository;

import com.smartschedule.entity.ScheduleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleTagRepository extends JpaRepository<ScheduleTag, Long> {
    List<ScheduleTag> findByUserId(Long userId);
    Optional<ScheduleTag> findByUserIdAndName(Long userId, String name);
}