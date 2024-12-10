package com.example.gameboxd.gameboxd_backend.dto.game;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.gameboxd.gameboxd_backend.dto.review.ReviewResponseDTO;

/**
 * DTO for returning game details from your API.
 */
@Data
public class GameResponseDTO {
    private UUID id;
    private int rawgId;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private List<String> genres;
    private List<String> platforms;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReviewResponseDTO> reviews = new ArrayList<>();
    private Double averageRating;
    private Long totalRatings;
}
