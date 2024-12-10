// Path: dto/developer/DeveloperResponseDTO.java

package com.example.gameboxd.gameboxd_backend.dto.developer;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning developer details from your API.
 */
@Data
public class DeveloperResponseDTO {
    private UUID id;
    private String name;
    private String slug;
    private int gamesCount;
    private String imageBackground;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
