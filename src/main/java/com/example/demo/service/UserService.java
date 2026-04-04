package com.example.demo.service;

import java.util.Date;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.LoginAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.UserNotFoundException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public List<User> getAll() {
        return repo.findAll();
    }

    public User getById(Long id) {
        return repo.findById(id).orElseThrow(UserNotFoundException::new);
    }

    private final PasswordEncoder passwordEncoder;



    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public void deleteById(Long id) {
        repo.findById(id).orElseThrow(UserNotFoundException::new);
        repo.deleteById(id);
    }

    public User createUser(String name) {

        if (name == null || name.isEmpty()) {
            throw new BadRequestException();
        }
        User user = new User();
        user.setName(name);
        return repo.save(user);
    }


    public User updateUser(Long id, String name) {

        if (name == null || name.isEmpty()) {
            throw new BadRequestException();
        }

        User user = repo.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.setName(name);
        return repo.save(user);
    }

    public UserResponseDto login(LoginRequestDto dto) {
        if (dto.getLogin() == null || dto.getLogin().isEmpty()) {
            throw new BadRequestException();
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new BadRequestException();
        }
        User user = repo.findByLogin(dto.getLogin());
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException();
        }

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setName(user.getName());
        responseDto.setEmail(user.getEmail());
        responseDto.setLogin(user.getLogin());
        responseDto.setCreatedAt(user.getCreatedAt());
        return responseDto;
    }

    public UserResponseDto register(RegisterRequestDto dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new BadRequestException();
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new BadRequestException();
        }
        if (dto.getLogin() == null || dto.getLogin().isEmpty()) {
            throw new BadRequestException();
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new BadRequestException();
        }
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