// Path: service/FollowersService.java

package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.follower.FollowRequestDTO;
import com.example.gameboxd.gameboxd_backend.dto.follower.FollowerResponseDTO;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.exception.AlreadyFollowingException;
import com.example.gameboxd.gameboxd_backend.exception.NotFollowingException;
import com.example.gameboxd.gameboxd_backend.model.FollowerId;
import com.example.gameboxd.gameboxd_backend.model.Followers;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.FollowersRepository;
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
public class FollowersService {

    private static final Logger logger = LoggerFactory.getLogger(FollowersService.class);

    private final FollowersRepository followersRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Allows a user to follow another user.
     *
     * @param followerId  the UUID of the follower
     * @param followRequestDTO the DTO containing the following user's ID
     * @throws ResourceNotFoundException if the following user does not exist
     * @throws AlreadyFollowingException if the follower is already following the user
     * @throws IllegalArgumentException if the follower tries to follow themselves
     */
    @Transactional
    public void followUser(UUID followerId, FollowRequestDTO followRequestDTO) {
        UUID followingId = followRequestDTO.getFollowingId();

        logger.info("User {} is attempting to follow user {}", followerId, followingId);

        if (followerId.equals(followingId)) {
            logger.warn("User {} attempted to follow themselves", followerId);
            throw new IllegalArgumentException("Users cannot follow themselves.");
        }

        // Check if the following user exists
        User followingUser = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User to follow not found with ID: " + followingId));

        // Check if already following
        FollowerId followerIdObj = new FollowerId(followerId, followingId);
        boolean exists = followersRepository.existsById(followerIdObj);
        if (exists) {
            logger.warn("User {} is already following user {}", followerId, followingId);
            throw new AlreadyFollowingException("You are already following this user.");
        }

        // Create new follower relationship
        Followers followers = Followers.builder()
                .id(followerIdObj)
                .follower(userRepository.findById(followerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Follower user not found with ID: " + followerId)))
                .following(followingUser)
                .createdAt(LocalDateTime.now())
                .build();

        followersRepository.save(followers);
        logger.info("User {} successfully followed user {}", followerId, followingId);
    }

    /**
     * Allows a user to unfollow another user.
     *
     * @param followerId the UUID of the follower
     * @param followRequestDTO the DTO containing the following user's ID
     * @throws NotFollowingException if the follower is not following the user
     */
    @Transactional
    public void unfollowUser(UUID followerId, FollowRequestDTO followRequestDTO) {
        UUID followingId = followRequestDTO.getFollowingId();

        logger.info("User {} is attempting to unfollow user {}", followerId, followingId);

        FollowerId followerIdObj = new FollowerId(followerId, followingId);
        Followers followers = followersRepository.findById(followerIdObj)
                .orElseThrow(() -> {
                    logger.warn("User {} is not following user {}", followerId, followingId);
                    return new NotFollowingException("You are not following this user.");
                });

        followersRepository.delete(followers);
        logger.info("User {} successfully unfollowed user {}", followerId, followingId);
    }

    /**
     * Retrieves all followers of a specific user.
     *
     * @param userId the UUID of the user
     * @return list of FollowerResponseDTO
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public List<FollowerResponseDTO> getFollowers(UUID userId) {
        logger.info("Retrieving followers for user {}", userId);

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Followers> followersList = followersRepository.findByFollowingId(userId);

        return followersList.stream()
                .map(followers -> modelMapper.map(followers.getFollower(), FollowerResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users that a specific user is following.
     *
     * @param userId the UUID of the user
     * @return list of FollowerResponseDTO
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public List<FollowerResponseDTO> getFollowing(UUID userId) {
        logger.info("Retrieving following users for user {}", userId);

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Followers> followingList = followersRepository.findByFollowerId(userId);

        return followingList.stream()
                .map(followers -> modelMapper.map(followers.getFollowing(), FollowerResponseDTO.class))
                .collect(Collectors.toList());
    }
}
