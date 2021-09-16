package com.example.springfilerest.service;



import com.example.springfilerest.model.User;

import java.util.List;

public interface UserService {
    User register(User user);

    User findByEmail(String email);

    List<User> getAll();

    User update(User user);

    User updateStatus(User user);

    User findByUserName(String username);

    User findById(Long id);

}
