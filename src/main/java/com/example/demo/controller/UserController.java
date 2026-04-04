package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PostMapping
    public User createUser(@RequestParam String name) {
        return service.createUser(name);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestParam String name) {
        return service.updateUser(id, name);
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody LoginRequestDto dto) {
        return service.login(dto);
    }
}