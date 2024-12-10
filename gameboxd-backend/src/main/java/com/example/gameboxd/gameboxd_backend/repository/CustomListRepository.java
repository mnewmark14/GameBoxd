package com.example.gameboxd.gameboxd_backend.repository;

import com.example.gameboxd.gameboxd_backend.model.CustomList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomListRepository extends JpaRepository<CustomList, UUID> {
    List<CustomList> findByUserId(UUID userId);
    Optional<CustomList> findByIdAndUserId(UUID listId, UUID userId);
    Optional<CustomList> findByUserIdAndIsDefault(UUID userId, boolean isDefault);
}
