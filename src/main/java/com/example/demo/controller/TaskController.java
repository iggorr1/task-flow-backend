package com.example.demo.controller;

import com.example.demo.dto.CreateTaskRequestDto;
import jakarta.validation.Valid;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.UpdateTaskRequestDto;
import com.example.demo.service.TaskService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody CreateTaskRequestDto dto) {

        return taskService.createTask(dto.getTitle(), dto.getDescription());    }

    @GetMapping
    public List<TaskResponseDto> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return taskService.getMyTasks(page, size, sort);
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

}