package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.rating.RatingDTO;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.Ratings;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.GamesRepository;
import com.example.gameboxd.gameboxd_backend.repository.RatingsRepository;
import com.example.gameboxd.gameboxd_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingsService {

    private static final Logger logger = LoggerFactory.getLogger(RatingsService.class);

    private final RatingsRepository ratingsRepository;
    private final UserRepository userRepository;
    private final GamesRepository gamesRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new rating for a game by a user.
     *
     * @param ratingDTO the DTO containing rating details
     * @return the created RatingDTO
     */
    @Transactional
    public RatingDTO createRating(RatingDTO ratingDTO) {
        logger.info("Creating rating for game ID: {} by user ID: {}", ratingDTO.getGameId(), ratingDTO.getUserId());

        // Fetch user and game entities
        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + ratingDTO.getUserId()));

        Games game = gamesRepository.findById(ratingDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + ratingDTO.getGameId()));

        // Check if the user has already rated the game
        ratingsRepository.findByUserIdAndGameId(user.getId(), game.getId()).ifPresent(existingRating -> {
            throw new IllegalArgumentException("User has already rated this game.");
        });

        // Create and save the new rating
        Ratings rating = Ratings.builder()
                .user(user)
                .game(game)
                .rating(ratingDTO.getRating())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ratings savedRating = ratingsRepository.save(rating);
        logger.info("Rating created with ID: {}", savedRating.getId());

        return mapToDTO(savedRating);
    }

    /**
     * Retrieves a rating by its UUID.
     *
     * @param ratingId the UUID of the rating
     * @return the RatingDTO
     */
    @Transactional(readOnly = true)
    public RatingDTO getRatingById(UUID ratingId) {
        logger.info("Fetching rating with ID: {}", ratingId);
        Ratings rating = ratingsRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));

        return mapToDTO(rating);
    }

    /**
     * Retrieves all ratings for a specific game.
     *
     * @param gameId the UUID of the game
     * @return list of RatingDTO
     */
    @Transactional(readOnly = true)
    public List<RatingDTO> getRatingsByGameId(UUID gameId) {
        logger.info("Fetching ratings for game ID: {}", gameId);
        List<Ratings> ratings = ratingsRepository.findByGameId(gameId);
        return ratings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all ratings made by a specific user.
     *
     * @param userId the UUID of the user
     * @return list of RatingDTO
     */
    @Transactional(readOnly = true)
    public List<RatingDTO> getRatingsByUserId(UUID userId) {
        logger.info("Fetching ratings by user ID: {}", userId);
        List<Ratings> ratings = ratingsRepository.findByUserId(userId);
        return ratings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing rating.
     *
     * @param ratingId the UUID of the rating to update
     * @param ratingDTO the DTO containing updated rating details
     * @return the updated RatingDTO
     */
    @Transactional
    public RatingDTO updateRating(UUID ratingId, RatingDTO ratingDTO) {
        logger.info("Updating rating with ID: {}", ratingId);
        Ratings rating = ratingsRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));

        rating.setRating(ratingDTO.getRating());
        rating.setUpdatedAt(LocalDateTime.now());

        Ratings updatedRating = ratingsRepository.save(rating);
        logger.info("Rating with ID: {} updated successfully", updatedRating.getId());

        return mapToDTO(updatedRating);
    }

    /**
     * Deletes a rating by its UUID.
     *
     * @param ratingId the UUID of the rating to delete
     */
    @Transactional
    public void deleteRating(UUID ratingId) {
        logger.info("Deleting rating with ID: {}", ratingId);
        Ratings rating = ratingsRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));

        ratingsRepository.delete(rating);
        logger.info("Rating with ID: {} deleted successfully", ratingId);
    }

    /**
     * Maps a Ratings entity to a RatingDTO.
     *
     * @param rating the Ratings entity
     * @return the RatingDTO
     */
    private RatingDTO mapToDTO(Ratings rating) {
        return modelMapper.map(rating, RatingDTO.class);
    }
}
