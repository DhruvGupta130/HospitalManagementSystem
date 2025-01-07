package com.example.system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String username;
    private UserRole role;
    private String message;
    private HttpStatus status;
}
