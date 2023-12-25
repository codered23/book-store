package com.example.bookstore.service;

import com.example.bookstore.model.User;

public interface UserService {
    User save(User user);

    User findByUsername(String username);

    void deleteById(Long id);
}
