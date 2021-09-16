package com.example.springfilerest.dto;


import com.example.springfilerest.model.User;
import lombok.Data;

@Data
public class UserDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }


}
