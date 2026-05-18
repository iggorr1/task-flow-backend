package com.igor.taskflow.service;

import com.igor.taskflow.dto.AdminStatsResponseDto;
import com.igor.taskflow.dto.AdminTaskResponseDto;
import com.igor.taskflow.dto.AdminUserResponseDto;
import com.igor.taskflow.entity.Task;
import com.igor.taskflow.entity.TaskStatus;
import com.igor.taskflow.entity.TelegramConnection;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.repository.TaskRepository;
import com.igor.taskflow.repository.TelegramConnectionRepository;
import com.igor.taskflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TelegramConnectionRepository telegramConnectionRepository;

    public AdminService(
            UserRepository userRepository,
            TaskRepository taskRepository,
            TelegramConnectionRepository telegramConnectionRepository
    ) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.telegramConnectionRepository = telegramConnectionRepository;
    }

    public List<AdminUserResponseDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToDto)
                .toList();
    }

    public AdminUserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapUserToDto(user);
    }

    public List<AdminTaskResponseDto> getTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapTaskToDto)
                .toList();
    }

    public AdminStatsResponseDto getStats() {
        List<Task> tasks = taskRepository.findAll();

        long todoTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.TODO)
                .count();

        long inProgressTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();

        long doneTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();

        return new AdminStatsResponseDto(
                userRepository.count(),
                taskRepository.count(),
                todoTasks,
                inProgressTasks,
                doneTasks
        );
    }

    private AdminUserResponseDto mapUserToDto(User user) {
        Optional<TelegramConnection> telegramConnectionOptional =
                telegramConnectionRepository.findByUser(user);

        TelegramConnection telegramConnection = telegramConnectionOptional.orElse(null);

        return new AdminUserResponseDto(
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getRole(),
                telegramConnection != null ? telegramConnection.getTelegramChatId() : null,
                telegramConnection != null ? telegramConnection.getTelegramUsername() : null,
                telegramConnection != null ? telegramConnection.getConnectedAt() : null
        );
    }

    private AdminTaskResponseDto mapTaskToDto(Task task) {
        return new AdminTaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.isCompleted(),
                task.isPinned(),
                task.getStatus(),
                task.getReminderAt(),
                task.isReminderSent(),
                task.getUser().getId(),
                task.getUser().getLogin()
        );
    }
}