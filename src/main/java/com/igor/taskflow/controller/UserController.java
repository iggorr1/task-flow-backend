package com.igor.taskflow.controller;

import com.igor.taskflow.dto.LoginRequestDto;
import com.igor.taskflow.dto.LoginResponseDto;
import com.igor.taskflow.dto.RegisterRequestDto;
import com.igor.taskflow.dto.UserResponseDto;
import com.igor.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
        return service.login(dto);
    }

    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto dto) {
        return service.register(dto);
    }
}