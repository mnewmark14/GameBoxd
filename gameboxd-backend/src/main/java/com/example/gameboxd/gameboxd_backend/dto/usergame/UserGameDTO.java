// Path: dto/usergame/UserGameDTO.java

package com.example.gameboxd.gameboxd_backend.dto.usergame;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a user's game collection entry.
 */
@Data
public class UserGameDTO {
    private UUID userId;
    private UUID gameId;

    @NotBlank(message = "Status is mandatory")
    @Pattern(regexp = "Playing|Completed|Plan to Play|Dropped", message = "Status must be one of: Playing, Completed, Plan to Play, Dropped")
    private String status;

    private LocalDateTime addedAt;
}
