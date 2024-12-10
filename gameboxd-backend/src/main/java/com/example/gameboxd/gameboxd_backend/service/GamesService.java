package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.game.ExternalGameDTO;
import com.example.gameboxd.gameboxd_backend.dto.game.GameDTO;
import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.rating.RatingRequestDTO;
import com.example.gameboxd.gameboxd_backend.dto.rating.RatingResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.ExternalGameListResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.GameListResponse;
import com.example.gameboxd.gameboxd_backend.dto.rawg.GenreDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.PlatformDTO;
import com.example.gameboxd.gameboxd_backend.dto.review.ReviewRequestDTO;
import com.example.gameboxd.gameboxd_backend.dto.review.ReviewResponseDTO;
import com.example.gameboxd.gameboxd_backend.exception.ExternalApiException;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.exception.UserNotFoundException;
import com.example.gameboxd.gameboxd_backend.model.CustomList;
import com.example.gameboxd.gameboxd_backend.model.GameLog;
import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.Ratings;
import com.example.gameboxd.gameboxd_backend.model.Reviews;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.CustomListRepository;
import com.example.gameboxd.gameboxd_backend.repository.GameLogRepository;
import com.example.gameboxd.gameboxd_backend.repository.GamesRepository;
import com.example.gameboxd.gameboxd_backend.repository.RatingsRepository;
import com.example.gameboxd.gameboxd_backend.repository.ReviewsRepository;
import com.example.gameboxd.gameboxd_backend.repository.UserRepository;
import com.example.gameboxd.gameboxd_backend.client.RawgApiClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GamesService {

    private static final Logger logger = LoggerFactory.getLogger(GamesService.class);

    private final GamesRepository gamesRepository;
    private final RawgApiClient rawgApiClient;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ReviewsRepository reviewsRepository;
    private final RatingsRepository ratingsRepository;
    private final String apiKey;
    private final CustomListRepository customListRepository;
    private final GameLogRepository gameLogsRepository;
    
    private WebClient webClient;
    
    @Autowired
    public GamesService(WebClient.Builder webClientBuilder, 
                        @Value("${rawg.api.key}") String apiKey, 
                        GamesRepository gamesRepository, 
                        RawgApiClient rawgApiClient, 
                        ModelMapper modelMapper, 
                        UserRepository userRepository, 
                        ReviewsRepository reviewsRepository, 
                        RatingsRepository ratingsRepository,
                        CustomListRepository customListRepository,
                        GameLogRepository gameLogsRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.rawg.io/api").build();
        this.apiKey = apiKey;
        this.gamesRepository = gamesRepository;
        this.rawgApiClient = rawgApiClient;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.reviewsRepository = reviewsRepository;
        this.ratingsRepository = ratingsRepository;
        this.customListRepository = customListRepository;
        this.gameLogsRepository = gameLogsRepository;
    }

   public Mono<ExternalGameListResponseDTO> getGamesFromRawg(int page, String searchQuery) {
        logger.info("Fetching games from RAWG API - Page: {}, Search Query: '{}'", page, searchQuery);

        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/games")
                            .queryParam("key", apiKey)
                            .queryParam("page", page);
                    if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                        uriBuilder.queryParam("search", searchQuery);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    logger.error("Client error when fetching games: {}", response.statusCode());
                    return Mono.error(new ExternalApiException("Client error when fetching games from RAWG API."));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    logger.error("Server error when fetching games: {}", response.statusCode());
                    return Mono.error(new ExternalApiException("Server error when fetching games from RAWG API."));
                })
                .bodyToMono(ExternalGameListResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ExternalApiException))
                .doOnSuccess(response -> logger.info("Successfully fetched games from RAWG API - Count: {}", response.getCount()))
                .doOnError(error -> logger.error("Error fetching games from RAWG API", error));
    }

    private GameResponseDTO mapToGameResponseDTO(ExternalGameDTO externalGame) {
        GameResponseDTO internalGame = new GameResponseDTO();
        internalGame.setRawgId(externalGame.getId());
        internalGame.setTitle(externalGame.getName());
        internalGame.setDescription("Description not available"); // RAWG might not provide description
        internalGame.setReleaseDate(externalGame.getReleased());
        internalGame.setPlatforms(convertPlatforms(externalGame.getPlatforms()));
        internalGame.setCoverImage(externalGame.getBackgroundImage());
        internalGame.setAverageRating(externalGame.getRating());
        internalGame.setTotalRatings((long) externalGame.getRatingsCount());
        internalGame.setCreatedAt(LocalDateTime.now());
        internalGame.setUpdatedAt(LocalDateTime.now());
        return internalGame;
    }

    

    
 
    /**
     * Retrieves all games with pagination from RAWG and maps to InternalGameResponseDTO.
     *
     * @param page       the page number (1-based)
     * @param searchQuery the search query
     * @return list of InternalGameResponseDTO
     */
    @Transactional
    public Mono<List<GameResponseDTO>> getAllGames(int page, String searchQuery) {
        logger.info("Retrieving all games from RAWG - Page: {}, Search Query: '{}'", page, searchQuery);
        return getGamesFromRawg(page, searchQuery)
                .map(response -> response.getResults().stream()
                        .map(this::mapToGameResponseDTO)
                        .collect(Collectors.toList()));
    }

    /**
     * Retrieves a game by its UUID.
     *
     * @param gameId the UUID of the game
     * @return GameResponseDTO
     * @throws ResourceNotFoundException if the game is not found
     */
    @Transactional(readOnly = true)
    public GameResponseDTO getGameById(UUID gameId) {
        logger.info("Retrieving game with ID: {}", gameId);
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));

        return modelMapper.map(game, GameResponseDTO.class);
    }

    /**
     * Fetches game details by RAWG ID.
     * If the game is not present in the database, fetch from RAWG, save it, and return details.
     *
     * @param rawgId the RAWG ID of the game
     * @return GameResponseDTO
     * @throws ResourceNotFoundException if the game is not found in RAWG API
     */
/**
     * Fetches game details by RAWG ID.
     * If the game is not present in the database, fetch from RAWG, save it, and return details.
     *
     * @param rawgId the RAWG ID of the game
     * @return GameResponseDTO
     * @throws ResourceNotFoundException if the game is not found in RAWG API
     */
    @Transactional
    public GameResponseDTO getGameByRawgId(int rawgId) {
        logger.info("Retrieving game with RAWG ID: {}", rawgId);

        // Check if the game exists in the database
        Optional<Games> existingGameOpt = gamesRepository.findByRawgId(rawgId);
        if (existingGameOpt.isPresent()) {
            logger.info("Game found in database with RAWG ID: {}", rawgId);
            return modelMapper.map(existingGameOpt.get(), GameResponseDTO.class);
        }

        // If not found, fetch from RAWG API
        logger.info("Game not found in database. Fetching from RAWG API...");
        ExternalGameDTO externalGame = rawgApiClient.getGameByRawgId(rawgId)
                .block(); // Blocking call for simplicity; consider using reactive patterns

        if (externalGame == null) {
            throw new ResourceNotFoundException("Game not found with RAWG ID: " + rawgId);
        }

        // Map ExternalGameDTO to Games entity
        Games game = mapExternalGameToEntity(externalGame);

        // Save to repository
        Games savedGame = gamesRepository.save(game);
        logger.info("Game saved to database with ID: {}", savedGame.getId());

        // Map to GameResponseDTO and return
        return modelMapper.map(savedGame, GameResponseDTO.class);
    }

        /**
     * Maps ExternalGameDTO to Games entity.
     *
     * @param externalGame the ExternalGameDTO
     * @return Games entity
     */
    private Games mapExternalGameToEntity(ExternalGameDTO externalGame) {
        return Games.builder()
                .rawgId(externalGame.getId())
                .title(externalGame.getName())
                .description(externalGame.getDescriptionRaw() != null ? externalGame.getDescriptionRaw() : "Description not available")
                .releaseDate(externalGame.getReleased())
                .coverImage(externalGame.getBackgroundImage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    
    
    /**
     * Creates a new game by fetching data from the RAWG API.
     *
     * @param rawgGameId the RAWG game ID
     * @return GameResponseDTO
     * @throws ExternalApiException if the RAWG API call fails
     */
    @Transactional
    public GameResponseDTO createGame(int rawgGameId) {
        logger.info("Creating game with RAWG ID: {}", rawgGameId);
    
        // Check if the game already exists
        if (gamesRepository.existsByRawgId(rawgGameId)) {
            throw new ExternalApiException("Game already exists with RAWG ID: " + rawgGameId);
        }
    
        // Fetch game data from RAWG API
        ExternalGameDTO externalGame = rawgApiClient.getGameByRawgId(rawgGameId)
                .block(); // Blocking call for simplicity; consider using reactive patterns
    
        if (externalGame == null) {
            throw new ExternalApiException("Failed to fetch game data from RAWG API.");
        }
    
        // Map ExternalGameDTO to Games entity
        Games game = mapExternalGameToEntity(externalGame);
    
        // Save to repository
        Games savedGame = gamesRepository.save(game);
        logger.info("Game created with ID: {}", savedGame.getId());
    
        return modelMapper.map(savedGame, GameResponseDTO.class);
    }
    
    /**
     * Updates an existing game.
     *
     * @param gameId the UUID of the game to update
     * @param gameDTO the GameDTO containing updated data
     * @return GameResponseDTO
     * @throws ResourceNotFoundException if the game is not found
     */
    @Transactional
    public GameResponseDTO updateGame(UUID gameId, GameDTO gameDTO) {
        logger.info("Updating game with ID: {}", gameId);
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));

        // Update fields
        game.setTitle(gameDTO.getName());
        game.setDescription(gameDTO.getDescription());
        game.setReleaseDate(gameDTO.getReleased());
        game.setCoverImage(gameDTO.getBackgroundImage());

        // Update timestamp
        game.setUpdatedAt(java.time.LocalDateTime.now());

        // Save changes
        Games updatedGame = gamesRepository.save(game);
        logger.info("Game with ID: {} updated successfully.", updatedGame.getId());

        return modelMapper.map(updatedGame, GameResponseDTO.class);
    }

    /**
     * Deletes a game by its UUID.
     *
     * @param gameId the UUID of the game to delete
     * @throws ResourceNotFoundException if the game is not found
     */
    @Transactional
    public void deleteGame(UUID gameId) {
        logger.info("Deleting game with ID: {}", gameId);
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));

        gamesRepository.delete(game);
        logger.info("Game with ID: {} deleted successfully.", gameId);
    }

     @Transactional(readOnly = true)
    public GameResponseDTO getGameDetails(UUID gameId) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));

         // Calculate average rating and total number of ratings
        Double averageRating = ratingsRepository.calculateAverageRatingForGame(gameId);
        Long totalRatings = ratingsRepository.countByGameId(gameId);

        List<Reviews> reviews = reviewsRepository.findByGame(game);


        GameResponseDTO gameResponse = modelMapper.map(game, GameResponseDTO.class);
        gameResponse.setAverageRating(averageRating != null ? averageRating : 0.0);
        gameResponse.setTotalRatings(totalRatings);
        if (reviews != null && !reviews.isEmpty()) {
            List<ReviewResponseDTO> reviewDTOs = reviews.stream()
                    .map(review -> modelMapper.map(review, ReviewResponseDTO.class))
                    .collect(Collectors.toList());
            gameResponse.setReviews(reviewDTOs);
        } else {
            gameResponse.setReviews(new ArrayList<>()); // Ensure it's an empty list
        }

        return gameResponse;
    }

    @Transactional
    public RatingResponseDTO submitRating(UUID gameId, UUID userId, RatingRequestDTO ratingRequest) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if user has already rated this game
        Ratings existingRating = ratingsRepository.findByUserAndGame(user, game).orElse(null);
        if (existingRating != null) {
            existingRating.setRating(ratingRequest.getRating());
            existingRating.setUpdatedAt(LocalDateTime.now());
            ratingsRepository.save(existingRating);
            return modelMapper.map(existingRating, RatingResponseDTO.class);
        }

        // Create new rating
        Ratings rating = Ratings.builder()
                .user(user)
                .game(game)
                .rating(ratingRequest.getRating())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Ratings savedRating = ratingsRepository.save(rating);

        Double newAverageRating = ratingsRepository.calculateAverageRatingForGame(gameId);
        Long newTotalRatings = ratingsRepository.countByGameId(gameId);

        game.setAverageRating(newAverageRating != null ? newAverageRating : 0.0);
        game.setTotalRatings(newTotalRatings != null ? newTotalRatings.intValue() : 0);
        gamesRepository.save(game);
        addGameToReviewedList(userId, game);

        return modelMapper.map(savedRating, RatingResponseDTO.class);
    }

    @Transactional
    public ReviewResponseDTO submitReview(UUID gameId, UUID userId, ReviewRequestDTO reviewRequest) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Create new review
        Reviews review = Reviews.builder()
                .user(user)
                .game(game)
                .reviewText(reviewRequest.getReviewText())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Reviews savedReview = reviewsRepository.save(review);

            addGameToReviewedList(userId, game);

        return modelMapper.map(savedReview, ReviewResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviews(UUID gameId) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));
        List<Reviews> reviews = reviewsRepository.findByGame(game);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Helper methods to convert genres and platforms from DTOs to JSON strings

    private List<String> convertGenres(List<GenreDTO> genres) {
        return genres.stream()
                    .map(GenreDTO::getName)
                    .collect(Collectors.toList());
    }

    private List<String> convertPlatforms(List<PlatformDTO> platforms) {
        return platforms.stream()
                    .filter(platformDTO -> platformDTO.getPlatform() != null && platformDTO.getPlatform().getName() != null)
                    .map(platformDTO -> platformDTO.getPlatform().getName())
                    .collect(Collectors.toList());
    }

    @Transactional
    public void addGameToReviewedList(UUID userId, Games game) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    
        // Fetch the default reviewed games list
        CustomList reviewedGamesList = user.getCustomLists().stream()
                .filter(CustomList::isDefault)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Default reviewed games list not found"));
    
        // Add game to the custom list if not already present
        if (!reviewedGamesList.getGames().contains(game)) {
            logger.info("Adding game '{}' to the reviewed list for user '{}'", game.getTitle(), userId);
            
            reviewedGamesList.getGames().add(game);
            game.getCustomLists().add(reviewedGamesList); // Manage relationship on the other side
    
            // Save changes
            customListRepository.save(reviewedGamesList);
            gamesRepository.save(game); // Ensure both entities are updated
        } else {
            logger.info("Game '{}' already exists in the reviewed list.", game.getTitle());
        }
    }

    public void logGame(UUID gameId, UUID userId, String status) {
        // Fetch the game and user objects
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
    
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
        // Check if a GameLog exists for this game and user
        Optional<GameLog> existingLog = gameLogsRepository.findByGameIdAndUserId(gameId, userId);
    
        GameLog log;
        if (existingLog.isPresent()) {
            log = existingLog.get(); // If exists, update the existing log
        } else {
            log = new GameLog(); // Otherwise, create a new log
            log.setGame(game);
            log.setUser(user);
        }
    
        // Set the status and updated time
        log.setStatus(status != null ? status : "not-logged");
        log.setUpdatedAt(java.time.LocalDateTime.now());
    
        // Save the log back to the repository
        gameLogsRepository.save(log);
    }
    
    

    public String getLoggedStatus(UUID gameId, UUID userId) {
        logger.info("Fetching status for gameId: {} and userId: {}", gameId, userId);
        return gameLogsRepository.findStatusByGameIdAndUserId(gameId, userId).orElse("not-logged");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserCustomLists(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Initialize custom lists
        Hibernate.initialize(user.getCustomLists());

        // Map each custom list to include its details and games
        return user.getCustomLists().stream().map(customList -> {
            Map<String, Object> listData = new HashMap<>();
            listData.put("id", customList.getId());
            listData.put("name", customList.getName());

            Hibernate.initialize(customList.getGames());
            List<Map<String, Object>> games = customList.getGames().stream()
                    .map(game -> {
                        Map<String, Object> gameData = new HashMap<>();
                        gameData.put("id", game.getId());
                        gameData.put("title", game.getTitle());
                        return gameData;
                    })
                    .collect(Collectors.toList());

            listData.put("games", games);
            return listData;
        }).collect(Collectors.toList());
    }

    
    
    
    
    



}
    
