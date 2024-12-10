package com.example.gameboxd.gameboxd_backend.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    @NotBlank
    private String reviewText;
}