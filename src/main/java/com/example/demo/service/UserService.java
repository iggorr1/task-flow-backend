package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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
        return repo.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public User createUser(String name) {
        User user = new User();
        user.setName(name);
        return repo.save(user);
    }

    public User updateUser(Long id, String name) {
        User user = repo.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        user.setName(name);
        return repo.save(user);
    }
}