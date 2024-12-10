package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.GameLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameLogRepository extends JpaRepository<GameLog, UUID> {

    @Query("SELECT g.status FROM GameLog g WHERE g.game.id = :gameId AND g.user.id = :userId")
    Optional<String> findStatusByGameIdAndUserId(@Param("gameId") UUID gameId, @Param("userId") UUID userId);
    
    
    @Query("SELECT g FROM GameLog g WHERE g.game.id = :gameId AND g.user.id = :userId")
    Optional<GameLog> findByGameIdAndUserId(@Param("gameId") UUID gameId, @Param("userId") UUID userId);


}
