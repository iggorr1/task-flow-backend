package com.example.demo.controller;

import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.UpdateTaskRequestDto;
import com.example.demo.service.TaskService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.dto.TaskResponseDto;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponseDto createTask(@RequestParam String title,
                                      @RequestParam String description) {
        return taskService.createTask(title, description);
    }

    @GetMapping
    public List<TaskResponseDto> getMyTasks() {
        return taskService.getMyTasks();
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id,
                                      @RequestBody UpdateTaskRequestDto request) {
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