
package com.example.gameboxd.gameboxd_backend.dto.rating;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a user's rating for a game.
 */
@Data
public class RatingDTO {
    private UUID id;
    private UUID userId;
    private UUID gameId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private int rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
