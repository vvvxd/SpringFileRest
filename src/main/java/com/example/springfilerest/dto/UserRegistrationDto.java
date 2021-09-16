package com.example.springfilerest.dto;


import com.example.springfilerest.model.User;
import lombok.Data;

@Data
public class UserRegistrationDto {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String login;

    public User dtoToUser() {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

}
