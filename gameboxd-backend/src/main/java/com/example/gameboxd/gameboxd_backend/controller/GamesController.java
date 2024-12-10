package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.game.GameDTO;
import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.rating.RatingRequestDTO;
import com.example.gameboxd.gameboxd_backend.dto.rating.RatingResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.GameListResponse;
import com.example.gameboxd.gameboxd_backend.dto.rawg.GameListResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.review.ReviewRequestDTO;
import com.example.gameboxd.gameboxd_backend.dto.review.ReviewResponseDTO;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.repository.RatingsRepository;
import com.example.gameboxd.gameboxd_backend.service.GamesService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Validated
public class GamesController {

    private final GamesService gamesService;
    private final ModelMapper modelMapper;
    private final RatingsRepository ratingsRepository;

    /**
     * Retrieves all games with pagination.
     *
     * @param page     the page number (default 0)
     * @param pageSize the number of games per page (default 10)
     * @return list of GameResponseDTO
     */
    @GetMapping
    public Mono<ResponseEntity<List<GameResponseDTO>>> getAllGames(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "") String search) {
        return gamesService.getAllGames(page, search)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a game by its UUID.
     *
     * @param gameId the UUID of the game
     * @return GameResponseDTO
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponseDTO> getGameById(@PathVariable UUID gameId) {
        GameResponseDTO game = gamesService.getGameById(gameId);
        return ResponseEntity.ok(game);
    }


    /**
     * Retrieves a game by its RAWG ID.
     *
     * @param rawgId the RAWG ID of the game
     * @return GameResponseDTO
     */
    @GetMapping("/rawg/{rawgId}")
    public ResponseEntity<GameResponseDTO> getGameByRawgId(@PathVariable int rawgId) {
        GameResponseDTO game = gamesService.getGameByRawgId(rawgId);
        return ResponseEntity.ok(game);
    }

    /**
     * Creates a new game by RAWG game ID.
     *
     * @param rawgGameId the RAWG game ID
     * @return GameResponseDTO
     */
    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(@RequestParam int rawgGameId) {
        GameResponseDTO createdGame = gamesService.createGame(rawgGameId);
        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
    }

    /**
     * Updates an existing game.
     *
     * @param gameId the UUID of the game to update
     * @param gameDTO the GameDTO containing updated data
     * @return GameResponseDTO
     */
    @PutMapping("/{gameId}")
    public ResponseEntity<GameResponseDTO> updateGame(
            @PathVariable UUID gameId,
            @Valid @RequestBody GameDTO gameDTO) {
        GameResponseDTO updatedGame = gamesService.updateGame(gameId, gameDTO);
        return ResponseEntity.ok(updatedGame);
    }

    /**
     * Deletes a game by its UUID.
     *
     * @param gameId the UUID of the game to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID gameId) {
        gamesService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }


    /**
     * GET /api/v1/games/details/{rawgId}
     * New endpoint for game details
     */
    @GetMapping("/details/{rawgId}")
    public Mono<ResponseEntity<GameResponseDTO>> getGameDetails(@PathVariable int rawgId) {
        return Mono.fromCallable(() -> gamesService.getGameByRawgId(rawgId))
                .map(game -> ResponseEntity.ok(game))
                .onErrorResume(ResourceNotFoundException.class, e -> {
                    // Return 404 if game not found
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .onErrorResume(e -> {
                    // Handle other errors
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }
        /**
     * Submit a rating for a game.
     */

        /**
     * Submit a rating for a game.
     */
    @PostMapping("/{gameId}/ratings")
    public ResponseEntity<RatingResponseDTO> submitRating(
            @PathVariable UUID gameId,
            @RequestBody RatingRequestDTO ratingRequest,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        RatingResponseDTO rating = gamesService.submitRating(gameId, userId, ratingRequest);
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

        /**
     * Fetch reviews for a game.
     */
    @GetMapping("/{gameId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable UUID gameId) {
        List<ReviewResponseDTO> reviews = gamesService.getReviews(gameId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{gameId}/reviews")
    public ResponseEntity<ReviewResponseDTO> submitReview(
            @PathVariable UUID gameId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ReviewResponseDTO review = gamesService.submitReview(gameId, userId, reviewRequest);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/{gameId}/ratings/summary")
    public ResponseEntity<Map<String, Object>> getRatingsSummary(@PathVariable UUID gameId) {
        Double averageRating = ratingsRepository.calculateAverageRatingForGame(gameId);
        Long totalRatings = ratingsRepository.countByGameId(gameId);

        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating != null ? averageRating : 0.0);
        response.put("totalRatings", totalRatings != null ? totalRatings : 0);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/log")
    public ResponseEntity<Void> logGame(
            @PathVariable UUID gameId,
            @RequestParam String status,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        gamesService.logGame(gameId, userId, status);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{gameId}/log")
    public ResponseEntity<String> getGameStatus(
            @PathVariable UUID gameId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        String status = gamesService.getLoggedStatus(gameId, userId);
        return ResponseEntity.ok(status);
    }
    
    
    

}
