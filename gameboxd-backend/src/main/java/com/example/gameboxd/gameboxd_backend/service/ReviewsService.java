package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.review.ReviewDTO;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.model.CustomList;
import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.Reviews;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.CustomListRepository;
import com.example.gameboxd.gameboxd_backend.repository.GamesRepository;
import com.example.gameboxd.gameboxd_backend.repository.ReviewsRepository;
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
public class ReviewsService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewsService.class);

    private final ReviewsRepository reviewsRepository;
    private final UserRepository userRepository;
    private final GamesRepository gamesRepository;
    private final ModelMapper modelMapper;
    private final CustomListRepository customListRepository;

    /**
     * Creates a new review for a game by a user.
     *
     * @param reviewDTO the DTO containing review details
     * @return the created ReviewDTO
     */
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        logger.info("Creating review for game ID: {} by user ID: {}", reviewDTO.getGameId(), reviewDTO.getUserId());

        // Fetch user and game entities
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + reviewDTO.getUserId()));

        Games game = gamesRepository.findById(reviewDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + reviewDTO.getGameId()));

        // Check if the user has already reviewed the game
        if (!reviewsRepository.findByUserIdAndGameId(user.getId(), game.getId()).isEmpty()) {
            throw new IllegalArgumentException("User has already reviewed this game.");
        }

        // Create and save the new review
        Reviews review = Reviews.builder()
                .user(user)
                .game(game)
                .reviewText(reviewDTO.getReviewText())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Reviews savedReview = reviewsRepository.save(review);
        logger.info("Review created with ID: {}", savedReview.getId());

        CustomList defaultList = customListRepository.findByUserIdAndIsDefault(user.getId(), true)
                .orElseThrow(() -> new ResourceNotFoundException("Default list not found for user with ID: " + user.getId()));

        defaultList.getGames().add(game);
        customListRepository.save(defaultList);

        return mapToDTO(savedReview);
    }

    /**
     * Retrieves a review by its UUID.
     *
     * @param reviewId the UUID of the review
     * @return the ReviewDTO
     */
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(UUID reviewId) {
        logger.info("Fetching review with ID: {}", reviewId);
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        return mapToDTO(review);
    }

    /**
     * Retrieves all reviews for a specific game.
     *
     * @param gameId the UUID of the game
     * @return list of ReviewDTO
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByGameId(UUID gameId) {
        logger.info("Fetching reviews for game ID: {}", gameId);
        List<Reviews> reviews = reviewsRepository.findByGameId(gameId);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUserId(UUID userId) {
        logger.info("Fetching reviews for game ID: {}", userId);
        List<Reviews> reviews = reviewsRepository.findByUserId(userId);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews made by a specific user.
     *
     * @param userId the UUID of the user
     * @return list of ReviewDTO
     */
    @Transactional
    public ReviewDTO updateReview(UUID reviewId, ReviewDTO reviewDTO, UUID userId) {
        logger.info("Updating review with ID: {}", reviewId);
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only update your own reviews.");
        }
    
        review.setReviewText(reviewDTO.getReviewText());
        review.setUpdatedAt(LocalDateTime.now());
    
        Reviews updatedReview = reviewsRepository.save(review);
        logger.info("Review with ID: {} updated successfully", updatedReview.getId());
    
        return modelMapper.map(updatedReview, ReviewDTO.class);
    }
    
    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        logger.info("Deleting review with ID: {}", reviewId);
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own reviews.");
        }
    
        reviewsRepository.delete(review);
        logger.info("Review with ID: {} deleted successfully", reviewId);
    }
    

    /**
     * Maps a Reviews entity to a ReviewDTO.
     *
     * @param review the Reviews entity
     * @return the ReviewDTO
     */
    private ReviewDTO mapToDTO(Reviews review) {
        return modelMapper.map(review, ReviewDTO.class);
    }
}
