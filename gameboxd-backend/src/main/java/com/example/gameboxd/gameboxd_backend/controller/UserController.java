package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.login.LoginResponse;
import com.example.gameboxd.gameboxd_backend.dto.users.UserProfileDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserRegistrationDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserUpdateDTO;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    /**
     * Registers a new user.
     *
     * @param registrationDTO the user registration data
     * @return the created user details
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        User user = userService.registerUser(registrationDTO);

        // Generate a token
        String token = UUID.randomUUID().toString();
        user.setAuthToken(token);
        userService.saveUser(user);

        // Map user to UserResponseDTO
        UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);

        // Return token and user details
        LoginResponse response = new LoginResponse(token, userDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUserProfile(
            @Valid @RequestBody UserUpdateDTO updateDTO,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        User updatedUser = userService.updateUser(userId, updateDTO);
        UserResponseDTO responseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
        return ResponseEntity.ok(responseDTO);
    }

        /**
     * Retrieves the profile of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UserProfileDTO userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/me/reviewed-rated-games")
    public ResponseEntity<List<GameResponseDTO>> getReviewedAndRatedGames(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<GameResponseDTO> games = userService.getUserReviewedAndRatedGames(userId);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/me/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Map<String, Object> statistics = userService.getUserStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/me/custom-lists")
    public ResponseEntity<List<Map<String, Object>>> getUserCustomLists(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Map<String, Object>> customLists = userService.getUserCustomLists(userId);
        return ResponseEntity.ok(customLists);
    }




}
