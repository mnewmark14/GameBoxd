package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.rating.RatingDTO;
import com.example.gameboxd.gameboxd_backend.service.RatingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingsController {

    private final RatingsService ratingsService;

    /**
     * Endpoint to create a new rating.
     *
     * @param ratingDTO the rating details
     * @return the created RatingDTO
     */
    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRating = ratingsService.createRating(ratingDTO);
        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a rating by its ID.
     *
     * @param ratingId the UUID of the rating
     * @return the RatingDTO
     */
    @GetMapping("/{ratingId}")
    public ResponseEntity<RatingDTO> getRatingById(@PathVariable UUID ratingId) {
        RatingDTO rating = ratingsService.getRatingById(ratingId);
        return ResponseEntity.ok(rating);
    }

    /**
     * Endpoint to retrieve all ratings for a specific game.
     *
     * @param gameId the UUID of the game
     * @return list of RatingDTO
     */
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByGameId(@PathVariable UUID gameId) {
        List<RatingDTO> ratings = ratingsService.getRatingsByGameId(gameId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Endpoint to retrieve all ratings made by a specific user.
     *
     * @param userId the UUID of the user
     * @return list of RatingDTO
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByUserId(@PathVariable UUID userId) {
        List<RatingDTO> ratings = ratingsService.getRatingsByUserId(userId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Endpoint to update an existing rating.
     *
     * @param ratingId the UUID of the rating to update
     * @param ratingDTO the updated rating details
     * @return the updated RatingDTO
     */
    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingDTO> updateRating(@PathVariable UUID ratingId,
                                                  @Valid @RequestBody RatingDTO ratingDTO) {
        RatingDTO updatedRating = ratingsService.updateRating(ratingId, ratingDTO);
        return ResponseEntity.ok(updatedRating);
    }

    /**
     * Endpoint to delete a rating.
     *
     * @param ratingId the UUID of the rating to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID ratingId) {
        ratingsService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }
}
