package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.entity.TelegramConnection;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.TelegramConnectionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskReminderScheduler {

    private final TaskRepository taskRepository;
    private final TelegramConnectionRepository telegramConnectionRepository;
    private final TelegramMessageService telegramMessageService;

    public TaskReminderScheduler(
            TaskRepository taskRepository,
            TelegramConnectionRepository telegramConnectionRepository,
            TelegramMessageService telegramMessageService
    ) {
        this.taskRepository = taskRepository;
        this.telegramConnectionRepository = telegramConnectionRepository;
        this.telegramMessageService = telegramMessageService;
    }

    @Scheduled(fixedRate = 60000)
    public void processDueReminders() {
        Date now = new Date();

        List<Task> dueTasks = taskRepository.findByReminderAtBeforeAndReminderSentFalse(now);

        for (Task task : dueTasks) {
            Optional<TelegramConnection> connectionOptional =
                    telegramConnectionRepository.findByUser(task.getUser());

            if (connectionOptional.isEmpty()) {
                System.out.println("Telegram is not connected for task: " + task.getId());
                continue;
            }

            TelegramConnection connection = connectionOptional.get();

            String message = buildReminderMessage(task);

            telegramMessageService.sendMessage(connection.getTelegramChatId(), message);

            task.setReminderSent(true);
            taskRepository.save(task);

            System.out.println("Telegram reminder sent for task: " + task.getId());
        }
    }

    private String buildReminderMessage(Task task) {
        String message = "Reminder: " + task.getTitle();

        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            message += "\n\n" + task.getDescription();
        }

        return message;
    }
}