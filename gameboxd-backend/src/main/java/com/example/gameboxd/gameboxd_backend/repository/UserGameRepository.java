package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.UserGame;
import com.example.gameboxd.gameboxd_backend.model.UserGameId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, UserGameId> {
    List<UserGame> findByUserId(UUID userId);
    List<UserGame> findByGameId(UUID gameId);
}
