package com.example.gameboxd.gameboxd_backend.dto.users;

import java.util.UUID;

import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.list.CustomListDTO;
import com.example.gameboxd.gameboxd_backend.dto.review.ReviewDTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class UserProfileDTO {
    private UUID id;
    private String username;
    private String avatarUrl;
    private String bio;
    private int followersCount;
    private int followingCount;
    private List<GameResponseDTO> reviewedGames;

    private List<CustomListDTO> customLists; // Add this
    private List<ReviewDTO> gameReviews;  // Add this
}