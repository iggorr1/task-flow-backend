package com.igor.taskflow.repository;


import com.igor.taskflow.entity.Task;
import com.igor.taskflow.entity.TaskStatus;
import com.igor.taskflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndCompleted(User user, boolean completed, Pageable pageable);
    Page<Task> findByUserAndCompletedAndTitleContainingIgnoreCase(
            User user,
            Boolean completed,
            String title,
            Pageable pageable
    );
    Page<Task> findByUserAndTitleContainingIgnoreCase(
            User user,
            String title,
            Pageable pageable
    );
    Page<Task> findByUserAndStatus(
            User user,
            TaskStatus status,
            Pageable pageable
    );

    Page<Task> findByUserAndStatusAndTitleContainingIgnoreCase(
            User user,
            TaskStatus status,
            String title,
            Pageable pageable
    );

    List<Task> findByReminderAtBeforeAndReminderSentFalse(Date now);

}