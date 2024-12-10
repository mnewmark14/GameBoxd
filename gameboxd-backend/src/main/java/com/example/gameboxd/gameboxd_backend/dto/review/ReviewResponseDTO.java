package com.example.gameboxd.gameboxd_backend.dto.review;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.gameboxd.gameboxd_backend.dto.users.UserResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDTO {
    private UUID id;
    private String reviewText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponseDTO user; // Include user details
}