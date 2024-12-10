// src/main/java/com/example/gameboxd/gameboxd_backend/dto/ratings/RatingRequestDTO.java
package com.example.gameboxd.gameboxd_backend.dto.rating;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequestDTO {
    @Min(1)
    @Max(5)
    private int rating;
}
