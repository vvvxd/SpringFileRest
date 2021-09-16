package com.example.springfilerest.dto;

import lombok.Data;

@Data
public class AuthenticationResponseDto {
    private String username;
    private String jwtToken;

}
