package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.TaskStatus;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.UpdateTaskReminderRequestDto;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody CreateTaskRequestDto dto) {

        return taskService.createTask(dto.getTitle(), dto.getDescription());    }

    @GetMapping
    public PagedResponseDto<TaskResponseDto> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status
    ) {
        return taskService.getMyTasks(page, size, sort, completed, title, status);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id,
                                      @RequestBody
                                      @Valid UpdateTaskRequestDto request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PatchMapping("/{id}/complete")
    public TaskResponseDto completeTask(@PathVariable Long id) {
        return taskService.completeTask(id);
    }

    @PatchMapping("/{id}/status")
    public TaskResponseDto updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequestDto request
    ) {
        return taskService.updateTaskStatus(id, request);
    }

    @PatchMapping("/{id}/pin")
    public TaskResponseDto togglePinTask(@PathVariable Long id) {
        return taskService.togglePinTask(id);
    }

    @PatchMapping("/{id}/reminder")
    public TaskResponseDto updateTaskReminder(
            @PathVariable Long id,
            @RequestBody UpdateTaskReminderRequestDto requestDto
    ) {
        return taskService.updateTaskReminder(id, requestDto.getReminderAt());
    }

}