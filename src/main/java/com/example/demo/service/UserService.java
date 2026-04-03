package com.example.demo.service;

import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.UserNotFoundException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> getAll() {
        return repo.findAll();
    }

    public User getById(Long id) {
        return repo.findById(id).orElseThrow(UserNotFoundException::new);
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


    public void register(RegisterRequestDto dto) {
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
    }

}