package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.review.ReviewDTO;
import com.example.gameboxd.gameboxd_backend.service.ReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewsController {

    private final ReviewsService reviewsService;

    /**
     * Endpoint to create a new review.
     *
     * @param reviewDTO the review details
     * @return the created ReviewDTO
     */
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO createdReview = reviewsService.createReview(reviewDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a review by its ID.
     *
     * @param reviewId the UUID of the review
     * @return the ReviewDTO
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable UUID reviewId) {
        ReviewDTO review = reviewsService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    /**
     * Endpoint to retrieve all reviews for a specific game.
     *
     * @param gameId the UUID of the game
     * @return list of ReviewDTO
     */
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByGameId(@PathVariable UUID gameId) {
        List<ReviewDTO> reviews = reviewsService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Endpoint to retrieve all reviews made by a specific user.
     *
     * @param userId the UUID of the user
     * @return list of ReviewDTO
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUserId(@PathVariable UUID userId) {
        List<ReviewDTO> reviews = reviewsService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
        @PathVariable UUID reviewId,
        @RequestBody @Valid ReviewDTO reviewDTO,
        Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName()); // Extract user ID from Authentication
        ReviewDTO updatedReview = reviewsService.updateReview(reviewId, reviewDTO, userId);
        return ResponseEntity.ok(updatedReview);
    }
    
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
        @PathVariable UUID reviewId,
        Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName()); // Extract user ID from Authentication
        reviewsService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
    
    
}
