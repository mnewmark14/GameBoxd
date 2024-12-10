// Path: dto/follower/FollowerDTO.java

package com.example.gameboxd.gameboxd_backend.dto.follower;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a follower relationship between users.
 */
@Data
public class FollowerDTO {
    private UUID followerId;
    private UUID followingId;
    private LocalDateTime createdAt;
}
