// Path: dto/review/ReviewDTO.java

package com.example.gameboxd.gameboxd_backend.dto.review;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a user's review for a game.
 */
@Data
public class ReviewDTO {
    private UUID id;
    private UUID userId;
    private UUID gameId;

    @NotBlank(message = "Review text is mandatory")
    private String reviewText;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
