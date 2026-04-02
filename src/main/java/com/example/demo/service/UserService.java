package com.example.demo.service;

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
        User user = repo.findById(id).orElseThrow(UserNotFoundException::new);
        user.setName(name);
        return repo.save(user);
    }
}