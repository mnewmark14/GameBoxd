package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GamesRepository extends JpaRepository<Games, UUID> {
    boolean existsByRawgId(int rawgId);
    Optional<Games> findByRawgId(Integer rawgId);

    @Query("SELECT g FROM Games g JOIN g.reviews r WHERE r.user.id = :userId")
    List<Games> findGamesReviewedByUser(@Param("userId") UUID userId);

    @Query("SELECT r.game FROM Ratings r WHERE r.user.id = :userId")
    List<Games> findGamesRatedByUser(@Param("userId") UUID userId);

    @Query("SELECT g.status FROM GameLog g WHERE g.game.id = :gameId AND g.user.id = :userId")
    Optional<String> findGameStatusByUserIdAndGameId(@Param("userId") UUID userId, @Param("gameId") UUID gameId);

}
