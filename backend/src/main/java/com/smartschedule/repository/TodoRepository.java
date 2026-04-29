package com.smartschedule.repository;

import com.smartschedule.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserIdOrderByImportantDescCreatedAtDesc(Long userId);

    List<Todo> findByUserIdAndCompletedOrderByImportantDescCreatedAtDesc(Long userId, Boolean completed);

    long countByUserIdAndCompleted(Long userId, Boolean completed);
}
