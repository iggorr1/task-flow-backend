package com.example.demo.service;

import com.example.demo.dto.PagedResponseDto;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.UpdateTaskRequestDto;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exception.TaskAccessDeniedException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.demo.exception.TaskNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;

@Service
public class TaskService {

    private static final String DEFAULT_SORT_FIELD = "createdAt";
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

        User user = getCurrentUser();

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);

    }

    public PagedResponseDto<TaskResponseDto> getMyTasks(int page, int size, String sort, Boolean completed, String title) {

        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }

        Sort sortObj = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        User user = getCurrentUser();

        Page<Task> taskPage;

        if (title != null && !title.isBlank()) {
            taskPage = taskRepository.findByUserAndTitleContainingIgnoreCase(
                    user,
                    title,
                    pageable
            );
        } else if (completed == null) {
            taskPage = taskRepository.findByUser(
                    user,
                    pageable
            );
        } else {
            taskPage = taskRepository.findByUserAndCompleted(
                    user,
                    completed,
                    pageable
            );
        }

        List<Task> tasks = taskPage.getContent();

        return new PagedResponseDto<>(
                tasks.stream()
                        .map(this::toDto)
                        .toList(),
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages()
        );
    }


    private Sort buildSort(String sort) {

        if (!sort.contains(",")) {
            return Sort.by(DEFAULT_SORT_FIELD).descending();
        }

        String[] parts = sort.split(",");
        String field = parts[0];
        String direction = parts[1].toLowerCase();

        if (direction.equals("asc")) {
            return Sort.by(field).ascending();
        } else {
            return Sort.by(field).descending();
        }
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

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());


        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    public void deleteTask(Long id) {
        Task task = getMyTaskById(id);
        taskRepository.delete(task);
    }

    public TaskResponseDto completeTask(Long id) {
        Task task = getMyTaskById(id);

        task.setCompleted(true);

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    private TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.isCompleted()
        );
    }

}

