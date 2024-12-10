// File: UserUpdateDTO.java
package com.example.gameboxd.gameboxd_backend.dto.users;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateDTO {
    @Size(max = 50, message = "Username must be at most 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String avatarUrl;
    private String bio;
}
