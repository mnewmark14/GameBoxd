// Path: dto/follower/FollowerResponseDTO.java

package com.example.gameboxd.gameboxd_backend.dto.follower;

import lombok.Data;

import java.util.UUID;

/**
 * DTO representing follower/following user details.
 */
@Data
public class FollowerResponseDTO {
    private UUID id;
    private String username;
    private String avatarUrl;
    private String bio;
}
