package com.igor.taskflow.service;

import com.igor.taskflow.dto.PagedResponseDto;
import com.igor.taskflow.dto.TaskResponseDto;
import com.igor.taskflow.dto.UpdateTaskRequestDto;
import com.igor.taskflow.dto.UpdateTaskStatusRequestDto;
import com.igor.taskflow.entity.Task;
import com.igor.taskflow.entity.TaskStatus;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.exception.TaskAccessDeniedException;
import com.igor.taskflow.exception.TaskNotFoundException;
import com.igor.taskflow.exception.UserNotFoundException;
import com.igor.taskflow.repository.TaskRepository;
import com.igor.taskflow.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        task.setStatus(TaskStatus.TODO);
        task.setReminderSent(false);

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    public PagedResponseDto<TaskResponseDto> getMyTasks(
            int page,
            int size,
            List<String> sort,
            Boolean completed,
            String title,
            TaskStatus status
    ) {

        if (sort == null || sort.isEmpty()) {
            sort = List.of("createdAt,desc");
        }

        Sort sortObj = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        User user = getCurrentUser();

        Page<Task> taskPage;

        if (status != null && title != null && !title.isBlank()) {
            taskPage = taskRepository.findByUserAndStatusAndTitleContainingIgnoreCase(
                    user,
                    status,
                    title,
                    pageable
            );

        } else if (status != null) {
            taskPage = taskRepository.findByUserAndStatus(
                    user,
                    status,
                    pageable
            );

        } else if (completed != null && title != null && !title.isBlank()) {
            taskPage = taskRepository.findByUserAndCompletedAndTitleContainingIgnoreCase(
                    user,
                    completed,
                    title,
                    pageable
            );

        } else if (title != null && !title.isBlank()) {
            taskPage = taskRepository.findByUserAndTitleContainingIgnoreCase(
                    user,
                    title,
                    pageable
            );

        } else if (completed != null) {
            taskPage = taskRepository.findByUserAndCompleted(
                    user,
                    completed,
                    pageable
            );

        } else {
            taskPage = taskRepository.findByUser(user, pageable);
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

    public TaskResponseDto getTaskById(Long id) {
        return toDto(getMyTaskById(id));
    }

    private Sort buildSort(List<String> sortParams) {

        List<Sort.Order> orders = new ArrayList<>();

        List<String> normalizedSortParams = new ArrayList<>();

        for (String sort : sortParams) {
            if (sort == null || sort.isBlank()) {
                continue;
            }

            if (sort.contains(",")) {
                String[] parts = sort.split(",");
                for (String part : parts) {
                    normalizedSortParams.add(part.trim());
                }
            } else {
                normalizedSortParams.add(sort.trim());
            }
        }

        for (int i = 0; i < normalizedSortParams.size(); i += 2) {
            String field = normalizedSortParams.get(i);

            String direction = "desc";
            if (i + 1 < normalizedSortParams.size()) {
                direction = normalizedSortParams.get(i + 1);
            }

            if (direction.equalsIgnoreCase("asc")) {
                orders.add(Sort.Order.asc(field));
            } else {
                orders.add(Sort.Order.desc(field));
            }
        }

        if (orders.isEmpty()) {
            return Sort.by("createdAt").descending();
        }

        return Sort.by(orders);
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
        task.setStatus(TaskStatus.DONE);

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    public TaskResponseDto updateTaskStatus(Long id, UpdateTaskStatusRequestDto request) {
        Task task = getMyTaskById(id);

        task.setStatus(request.getStatus());

        if (request.getStatus() == TaskStatus.DONE) {
            task.setCompleted(true);
        } else {
            task.setCompleted(false);
        }

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    public TaskResponseDto togglePinTask(Long id) {
        Task task = getMyTaskById(id);

        task.setPinned(!task.isPinned());

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    public TaskResponseDto updateTaskReminder(Long taskId, Date reminderAt) {
        Task task = getMyTaskById(taskId);

        task.setReminderAt(reminderAt);
        task.setReminderSent(false);

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    public TaskResponseDto deleteTaskReminder(Long taskId) {
        Task task = getMyTaskById(taskId);

        task.setReminderAt(null);
        task.setReminderSent(false);

        Task savedTask = taskRepository.save(task);

        return toDto(savedTask);
    }

    private TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.isCompleted(),
                task.isPinned(),
                task.getStatus(),
                task.getReminderAt(),
                task.isReminderSent()
        );
    }


}

