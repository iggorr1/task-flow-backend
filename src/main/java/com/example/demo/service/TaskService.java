package com.example.demo.service;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.UpdateTaskRequestDto;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.TaskAccessDeniedException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.demo.exception.TaskNotFoundException;

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

        return userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
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
                savedTask.getCreatedAt(),
                savedTask.isCompleted()
        );

    }

    public List<TaskResponseDto> getMyTasks() {
        User user = getCurrentUser();

        return taskRepository.findByUser(user).stream()
                .map(task -> new TaskResponseDto(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getCreatedAt(),
                        task.isCompleted()
                ))
                .toList();
    }

    private Task getMyTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        User currentUser = getCurrentUser();

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new TaskAccessDeniedException();
        }

        return task;
    }

    public TaskResponseDto updateTask(Long id, UpdateTaskRequestDto request) {
        Task task = getMyTaskById(id);

        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new BadRequestException();
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());


        Task savedTask = taskRepository.save(task);

        return new TaskResponseDto(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getCreatedAt(),
                savedTask.isCompleted()
        );
    }

    public void deleteTask(Long id) {
        Task task = getMyTaskById(id);
        taskRepository.delete(task);
    }

    public TaskResponseDto completeTask(Long id) {
        Task task = getMyTaskById(id);

        task.setCompleted(true);

        Task savedTask = taskRepository.save(task);

        return new TaskResponseDto(
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getCreatedAt(),
                savedTask.isCompleted()
        );
    }

}

