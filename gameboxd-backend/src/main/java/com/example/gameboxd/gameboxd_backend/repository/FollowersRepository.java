package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.FollowerId;
import com.example.gameboxd.gameboxd_backend.model.Followers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, FollowerId> {
    List<Followers> findByFollowingId(UUID followingId);
    List<Followers> findByFollowerId(UUID followerId);
    Optional<Followers> findById_FollowerUUIDAndId_FollowingId(UUID followerUUID, UUID followingId);
}
