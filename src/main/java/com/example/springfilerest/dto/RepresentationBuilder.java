package com.example.springfilerest.dto;

import com.example.springfilerest.model.User;

public final class RepresentationBuilder {

    private RepresentationBuilder() {
    }

    public static UserDto createResponseForUser(User user) {
        return new UserDto(user);
    }

    public static AdminUserDto createResponseForAdmin(User user) {
        return new AdminUserDto(user);
    }

}
