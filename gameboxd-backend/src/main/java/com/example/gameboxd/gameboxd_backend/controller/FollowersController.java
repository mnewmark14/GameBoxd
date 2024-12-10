package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.follower.FollowRequestDTO;
import com.example.gameboxd.gameboxd_backend.dto.follower.FollowerResponseDTO;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.UserRepository;
import com.example.gameboxd.gameboxd_backend.service.FollowersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/followers")
@RequiredArgsConstructor
public class FollowersController {

    private final FollowersService followersService;

    /**
     * Endpoint for the authenticated user to follow another user.
     *
     * @param followRequestDTO the DTO containing the following user's ID
     * @param authentication    the authentication object containing the current user's details
     * @return ResponseEntity with status CREATED
     */
    @PostMapping("/follow")
    public ResponseEntity<Void> followUser(@Valid @RequestBody FollowRequestDTO followRequestDTO,
                                          Authentication authentication) {
        UUID followerId = getCurrentUserId(authentication);
        followersService.followUser(followerId, followRequestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Endpoint for the authenticated user to unfollow another user.
     *
     * @param followRequestDTO the DTO containing the following user's ID
     * @param authentication    the authentication object containing the current user's details
     * @return ResponseEntity with status NO_CONTENT
     */
    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(@Valid @RequestBody FollowRequestDTO followRequestDTO,
                                            Authentication authentication) {
        UUID followerId = getCurrentUserId(authentication);
        followersService.unfollowUser(followerId, followRequestDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint to retrieve all followers of a specific user.
     *
     * @param userId the UUID of the user
     * @return list of FollowerResponseDTO
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowerResponseDTO>> getFollowers(@PathVariable UUID userId) {
        List<FollowerResponseDTO> followers = followersService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    /**
     * Endpoint to retrieve all users that a specific user is following.
     *
     * @param userId the UUID of the user
     * @return list of FollowerResponseDTO
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<FollowerResponseDTO>> getFollowing(@PathVariable UUID userId) {
        List<FollowerResponseDTO> following = followersService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    /**
     * Utility method to extract the current authenticated user's UUID.
     *
     * @param authentication the authentication object
     * @return UUID of the current user
     */
    private UUID getCurrentUserId(Authentication authentication) {
        return (UUID) authentication.getPrincipal();
    }
}
