package com.example.demo.controller;

import com.example.demo.entity.Task;
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
    public Task createTask(@RequestParam String title,
                           @RequestParam String description) {
        return taskService.createTask(title, description);
    }

    @GetMapping
    public List<Task> getMyTasks() {
        return taskService.getMyTasks();
    }
}