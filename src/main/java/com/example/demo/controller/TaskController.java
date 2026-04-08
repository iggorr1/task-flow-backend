package com.example.demo.controller;

import com.example.demo.dto.TaskResponseDto;
import com.example.demo.service.TaskService;
import org.springframework.web.bind.annotation.*;

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
}