package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.login.LoginRequest;
import com.example.gameboxd.gameboxd_backend.dto.login.LoginResponse;
import com.example.gameboxd.gameboxd_backend.dto.users.UserResponseDTO;
import com.example.gameboxd.gameboxd_backend.exception.InvalidCredentialsException;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    /**
     * Authenticates a user and returns a token.
     *
     * @param loginRequest the login credentials
     * @return the authentication token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
    
        // Generate a simple random token
        String token = generateToken();
    
        // Save the token to the user's record
        user.setAuthToken(token);
        userService.saveUser(user);
    
        // Map user to UserResponseDTO
        UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
    
        return ResponseEntity.ok(new LoginResponse(token, userDTO));
    }
    

    /**
     * Logs out a user by invalidating their token.
     *
     * @param authorization the Authorization header containing the token
     * @return a response indicating successful logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            userService.invalidateToken(token);
            return ResponseEntity.noContent().build();
        }
        throw new InvalidCredentialsException("Invalid Authorization header.");
    }

    /**
     * Generates a random alphanumeric token.
     *
     * @return the generated token
     */
    private String generateToken() {
        return UUID.randomUUID().toString() + "-" + ThreadLocalRandom.current().nextInt(100000, 999999);
    }
}
