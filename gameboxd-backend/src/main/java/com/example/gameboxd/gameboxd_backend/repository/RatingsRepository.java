package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.Ratings;
import com.example.gameboxd.gameboxd_backend.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, UUID> {
    List<Ratings> findByGameId(UUID gameId);
    List<Ratings> findByUserId(UUID userId);
    Optional<Ratings> findByUserIdAndGameId(UUID userId, UUID gameId);
    Optional<Ratings> findByUserAndGame(User user, Games game);

    @Query("SELECT AVG(r.rating) FROM Ratings r WHERE r.game.id = :gameId")
    Double calculateAverageRatingForGame(@Param("gameId") UUID gameId);

    @Query("SELECT COUNT(r) FROM Ratings r WHERE r.game.id = :gameId")
    Long countByGameId(@Param("gameId") UUID gameId);

    @Query("SELECT r.rating, COUNT(r) FROM Ratings r WHERE r.user.id = :userId GROUP BY r.rating")
    List<Object[]> findRatingDistributionByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM Ratings r WHERE r.user.id = :userId")
    int countByUserId(UUID userId);
}


