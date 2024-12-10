package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, UUID> {
    @Query("SELECT r FROM Reviews r WHERE r.game.id = :gameId")
    List<Reviews> findByGameId(@Param("gameId") UUID gameId);
    List<Reviews> findByUserId(UUID userId);
    List<Reviews> findByUserIdAndGameId(UUID gameId, UUID userId);
    List<Reviews> findByGame(Games game);

    @Query("SELECT COUNT(r) FROM Reviews r WHERE r.user.id = :userId")
    int countByUserId(UUID userId);
}
