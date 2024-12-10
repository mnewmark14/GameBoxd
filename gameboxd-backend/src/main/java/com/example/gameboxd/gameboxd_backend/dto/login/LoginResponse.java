// Path: dto/LoginResponse.java

package com.example.gameboxd.gameboxd_backend.dto.login;

import com.example.gameboxd.gameboxd_backend.dto.users.UserResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for sending login response containing the authentication token.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserResponseDTO user;
}
