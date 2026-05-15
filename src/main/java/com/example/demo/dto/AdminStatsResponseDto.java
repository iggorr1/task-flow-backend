package com.example.demo.dto;

public class AdminStatsResponseDto {

    private long totalUsers;
    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long doneTasks;

    public AdminStatsResponseDto(long totalUsers, long totalTasks, long todoTasks, long inProgressTasks, long doneTasks) {
        this.totalUsers = totalUsers;
        this.totalTasks = totalTasks;
        this.todoTasks = todoTasks;
        this.inProgressTasks = inProgressTasks;
        this.doneTasks = doneTasks;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public long getTodoTasks() {
        return todoTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public long getDoneTasks() {
        return doneTasks;
    }
}