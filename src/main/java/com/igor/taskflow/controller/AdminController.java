package com.igor.taskflow.controller;

import com.igor.taskflow.dto.AdminStatsResponseDto;
import com.igor.taskflow.dto.AdminTaskResponseDto;
import com.igor.taskflow.dto.AdminUserResponseDto;
import com.igor.taskflow.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/test")
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping("/users")
    public List<AdminUserResponseDto> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/users/{id}")
    public AdminUserResponseDto getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @GetMapping("/tasks")
    public List<AdminTaskResponseDto> getTasks() {
        return adminService.getTasks();
    }

    @GetMapping("/stats")
    public AdminStatsResponseDto getStats() {
        return adminService.getStats();
    }
}