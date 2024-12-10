// Path: service/UserService.java

package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserProfileDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserRegistrationDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserUpdateDTO;
import com.example.gameboxd.gameboxd_backend.exception.UserAlreadyExistsException;
import com.example.gameboxd.gameboxd_backend.exception.UserNotFoundException;
import com.example.gameboxd.gameboxd_backend.exception.InvalidCredentialsException;
import com.example.gameboxd.gameboxd_backend.model.CustomList;
import com.example.gameboxd.gameboxd_backend.model.FollowerId;
import com.example.gameboxd.gameboxd_backend.model.Followers;
import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.CustomListRepository;
import com.example.gameboxd.gameboxd_backend.repository.FollowersRepository;
import com.example.gameboxd.gameboxd_backend.repository.GamesRepository;
import com.example.gameboxd.gameboxd_backend.repository.RatingsRepository;
import com.example.gameboxd.gameboxd_backend.repository.ReviewsRepository;
import com.example.gameboxd.gameboxd_backend.repository.UserRepository;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomListRepository customListRepository;
    private final ModelMapper modelMapper;
    private final FollowersRepository followersRepository;
    private final GamesRepository gamesRepository;
    private final ReviewsRepository reviewsRepository;
    private final RatingsRepository ratingsRepository;
    /**
     * Registers a new user.
     *
     * @param registrationDTO the DTO containing user registration details
     * @return the registered User entity
     * @throws UserAlreadyExistsException if the username or email is already in use
     */
    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        logger.info("Attempting to register user with username: {}", registrationDTO.getUsername());

        // Check if username already exists
        Optional<User> existingUserByUsername = userRepository.findByUsername(registrationDTO.getUsername());
        if (existingUserByUsername.isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists", registrationDTO.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }

        // Check if email already exists
        Optional<User> existingUserByEmail = userRepository.findByEmail(registrationDTO.getEmail());
        if (existingUserByEmail.isPresent()) {
            logger.warn("Registration failed: Email '{}' already in use", registrationDTO.getEmail());
            throw new UserAlreadyExistsException("Email already in use");
        }

        // Create new User entity
        User user = User.builder()
                .username(registrationDTO.getUsername())
                .email(registrationDTO.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDTO.getPassword()))
                .avatarUrl(registrationDTO.getAvatarUrl())
                .bio(registrationDTO.getBio())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        // Save user to the repository
        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully with ID: {}", savedUser.getUsername(), savedUser.getId());

        CustomList defaultList = CustomList.builder()
            .user(savedUser)
            .name("Reviewed Games")
            .isDefault(true)
            .games(new HashSet<>()) // Initially empty; will populate as reviews are added
            .build();

        customListRepository.save(defaultList);
        return savedUser;
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username the username
     * @param password the raw password
     * @return the authenticated User entity
     * @throws InvalidCredentialsException if authentication fails
     */
    @Transactional
    public User authenticateUser(String username, String password) {
        logger.info("Authenticating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Authentication failed for user: {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        logger.info("User '{}' authenticated successfully", username);
        return user;
    }

    /**
     * Saves or updates a user entity.
     *
     * @param user the User entity
     * @return the saved User entity
     */
    @Transactional
    public User saveUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Invalidates a user's authentication token.
     *
     * @param token the authentication token
     * @throws UserNotFoundException if the token is invalid
     */
    @Transactional
    public void invalidateToken(String token) {
        logger.info("Invalidating token: {}", token);
        User user = userRepository.findByAuthToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid token"));

        user.setAuthToken(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        logger.info("Token invalidated for user: {}", user.getUsername());
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the UUID of the user
     * @return an Optional containing the User if found, else empty
     */
    public Optional<User> getUserById(UUID userId) {
        logger.info("Retrieving user with ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Updates the details of an existing user.
     *
     * @param userId    the UUID of the user to update
     * @param updateDTO the DTO containing updated user details
     * @return the updated User entity
     * @throws UserNotFoundException if the user with the given ID does not exist
     */
    @Transactional
    public User updateUser(UUID userId, UserUpdateDTO updateDTO) {
        logger.info("Updating user with ID: {}", userId);

        // Retrieve the user or throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Update failed: User with ID '{}' not found", userId);
                    return new UserNotFoundException("User not found");
                });

        // Update fields if present in DTO
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().isEmpty()) {
            user.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().isEmpty()) {
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(updateDTO.getPassword()));
        }

        if (updateDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(updateDTO.getAvatarUrl());
        }

        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }

        // Update the timestamp
        user.setUpdatedAt(LocalDateTime.now());

        // Save the updated user
        User updatedUser = userRepository.save(user);
        logger.info("User with ID '{}' updated successfully", userId);

        return updatedUser;
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId the UUID of the user to delete
     * @throws UserNotFoundException if the user with the given ID does not exist
     */
    @Transactional
    public void deleteUser(UUID userId) {
        logger.info("Attempting to delete user with ID: {}", userId);

        // Check if user exists
        if (!userRepository.existsById(userId)) {
            logger.warn("Deletion failed: User with ID '{}' not found", userId);
            throw new UserNotFoundException("User not found");
        }

        // Delete the user
        userRepository.deleteById(userId);
        logger.info("User with ID '{}' deleted successfully", userId);
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    
        // Initialize collections if fetch type is LAZY
        Hibernate.initialize(user.getFollowers());
        Hibernate.initialize(user.getFollowing());
        Hibernate.initialize(user.getCustomLists());
    
        // Map to UserProfileDTO
        UserProfileDTO userProfile = modelMapper.map(user, UserProfileDTO.class);
    
        // Calculate followers and following counts
        userProfile.setFollowersCount(user.getFollowers().size());
        userProfile.setFollowingCount(user.getFollowing().size());
    
        // Retrieve the default custom list (e.g., "Reviewed Games")
        Optional<CustomList> defaultListOpt = user.getCustomLists().stream()
                .filter(CustomList::isDefault)
                .findFirst();
    
        if (defaultListOpt.isPresent()) {
            CustomList defaultList = defaultListOpt.get();
            Hibernate.initialize(defaultList.getGames()); // Ensure games are initialized
    
            // Map games to GameResponseDTO
            List<GameResponseDTO> reviewedGames = defaultList.getGames().stream()
                    .map(game -> modelMapper.map(game, GameResponseDTO.class))
                    .collect(Collectors.toList());
            userProfile.setReviewedGames(reviewedGames);
        } else {
            userProfile.setReviewedGames(Collections.emptyList());
        }
    
        return userProfile;
    }
    
    

    @Transactional
    public void followUser(UUID currentUserId, UUID userIdToFollow) {
        if (currentUserId.equals(userIdToFollow)) {
            throw new IllegalArgumentException("Users cannot follow themselves.");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        User userToFollow = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new UserNotFoundException("User to follow not found"));

        // Check if the relationship already exists
        FollowerId followerId = new FollowerId(currentUser.getId(), userToFollow.getId());
        boolean alreadyFollowing = followersRepository.existsById(followerId);

        if (alreadyFollowing) {
            throw new IllegalStateException("You are already following this user.");
        }

        // Create new Followers entity
        Followers followers = Followers.builder()
                .id(followerId)
                .follower(currentUser)
                .following(userToFollow)
                .build();

        // Add to collections
        currentUser.getFollowing().add(followers);
        userToFollow.getFollowers().add(followers);

        // Save the changes
        followersRepository.save(followers);
    }

    @Transactional
    public void unfollowUser(UUID currentUserId, UUID userIdToUnfollow) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        User userToUnfollow = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new UserNotFoundException("User to unfollow not found"));

        FollowerId followerId = new FollowerId(currentUser.getId(), userToUnfollow.getId());
        Followers followers = followersRepository.findById(followerId)
                .orElseThrow(() -> new IllegalStateException("You are not following this user."));

        // Remove from collections
        currentUser.getFollowing().remove(followers);
        userToUnfollow.getFollowers().remove(followers);

        // Delete the relationship
        followersRepository.delete(followers);
    }

    @Transactional(readOnly = true)
    public List<GameResponseDTO> getUserReviewedAndRatedGames(UUID userId) {
        List<Games> games = gamesRepository.findGamesRatedByUser(userId);

        // Convert to DTOs
        return games.stream()
            .map(game -> modelMapper.map(game, GameResponseDTO.class))
            .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        long totalGamesLogged = reviewsRepository.countByUserId(userId) + ratingsRepository.countByUserId(userId);

        // Distribution of ratings
        List<Object[]> ratingDistribution = ratingsRepository.findRatingDistributionByUser(userId);
        Map<Integer, Long> distribution = ratingDistribution.stream()
            .collect(Collectors.toMap(
                obj -> (Integer) obj[0], // Rating value
                obj -> (Long) obj[1]    // Count
            ));

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalGamesLogged", totalGamesLogged);
        statistics.put("ratingDistribution", distribution);

        return statistics;
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
