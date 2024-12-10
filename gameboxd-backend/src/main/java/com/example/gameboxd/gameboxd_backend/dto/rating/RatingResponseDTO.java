package com.example.gameboxd.gameboxd_backend.dto.rating;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingResponseDTO {
    private UUID id;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}