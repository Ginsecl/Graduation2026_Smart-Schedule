package com.smartschedule.repository;

import com.smartschedule.entity.ScheduleTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleTagRelationRepository extends JpaRepository<ScheduleTagRelation, ScheduleTagRelation.ScheduleTagRelationId> {
    void deleteByScheduleId(Long scheduleId);
}