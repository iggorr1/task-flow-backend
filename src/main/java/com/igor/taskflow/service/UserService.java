package com.igor.taskflow.service;

import com.igor.taskflow.dto.LoginRequestDto;
import com.igor.taskflow.dto.LoginResponseDto;
import com.igor.taskflow.dto.RegisterRequestDto;
import com.igor.taskflow.dto.UserResponseDto;
import com.igor.taskflow.entity.Role;
import com.igor.taskflow.entity.User;
import com.igor.taskflow.exception.BadRequestException;
import com.igor.taskflow.exception.EmailAlreadyExistsException;
import com.igor.taskflow.exception.LoginAlreadyExistsException;
import com.igor.taskflow.exception.UserNotFoundException;
import com.igor.taskflow.repository.UserRepository;
import com.igor.taskflow.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(JwtService jwtService, UserRepository repo, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(LoginRequestDto dto) {


        User user = repo.findByLogin(dto.getLogin())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException();
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                new ArrayList<>()
        );

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDto(token);
    }

    public UserResponseDto register(RegisterRequestDto dto) {

        if (repo.existsByLogin(dto.getLogin())) {
            throw new LoginAlreadyExistsException();
        }

        if (repo.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setRole(Role.USER);

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(hashedPassword);

        user.setCreatedAt(new Date());

        User savedUser = repo.save(user);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(savedUser.getId());
        responseDto.setName(savedUser.getName());
        responseDto.setEmail(savedUser.getEmail());
        responseDto.setLogin(savedUser.getLogin());
        responseDto.setCreatedAt(savedUser.getCreatedAt());

        return responseDto;
    }
}