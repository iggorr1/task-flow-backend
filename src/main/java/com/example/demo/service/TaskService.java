package com.example.demo.service;

import com.example.demo.dto.TaskResponseDto;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String login = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByLogin(login);
    }

    public TaskResponseDto createTask(String title, String description) {
        if (title == null || title.isEmpty()) {
            throw new BadRequestException();
        }

        User user = getCurrentUser();

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCreatedAt(new Date());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return new TaskResponseDto(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getCreatedAt()
        );
    }
}