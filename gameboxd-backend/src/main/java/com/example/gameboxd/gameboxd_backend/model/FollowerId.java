package com.example.gameboxd.gameboxd_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FollowerId implements Serializable {

    @Column(name = "follower_id")
    private UUID followerUUID;

    @Column(name = "following_id")
    private UUID followingId;

    // Getters and Setters
    public UUID getFollowerUUID() {
        return followerUUID;
    }

    public void setFollowerUUID(UUID followerUUID) {
        this.followerUUID = followerUUID;
    }

    public UUID getFollowingId() {
        return followingId;
    }

    public void setFollowingId(UUID followingId) {
        this.followingId = followingId;
    }
}
