// Path: dto/follower/FollowRequestDTO.java

package com.example.gameboxd.gameboxd_backend.dto.follower;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO for follow/unfollow requests.
 */
@Data
public class FollowRequestDTO {
    
    @NotNull(message = "Following user ID is mandatory")
    private UUID followingId;
}
