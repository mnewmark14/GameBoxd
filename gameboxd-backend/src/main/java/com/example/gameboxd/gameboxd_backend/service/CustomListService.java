package com.example.gameboxd.gameboxd_backend.service;

import com.example.gameboxd.gameboxd_backend.dto.list.CustomListDTO;
import com.example.gameboxd.gameboxd_backend.dto.list.CreateCustomListDTO;
import com.example.gameboxd.gameboxd_backend.exception.ResourceNotFoundException;
import com.example.gameboxd.gameboxd_backend.model.CustomList;
import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.example.gameboxd.gameboxd_backend.repository.CustomListRepository;
import com.example.gameboxd.gameboxd_backend.repository.GamesRepository;
import com.example.gameboxd.gameboxd_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomListService {

    private final CustomListRepository customListRepository;
    private final UserRepository userRepository;
    private final GamesRepository gamesRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new custom list for the user.
     *
     * @param userId             the UUID of the user
     * @param createCustomListDTO the DTO containing list details
     * @return the created CustomListDTO
     */
    @Transactional
    public CustomListDTO createCustomList(UUID userId, CreateCustomListDTO createCustomListDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        CustomList customList = CustomList.builder()
                .user(user)
                .name(createCustomListDTO.getName())
                .isDefault(false) // Custom lists are not default
                .games(new HashSet<>())
                .build();

        CustomList savedList = customListRepository.save(customList);
        return convertToDTO(savedList);
    }

    /**
     * Retrieves all custom lists for the user.
     *
     * @param userId the UUID of the user
     * @return list of CustomListDTO
     */
    @Transactional(readOnly = true)
    public List<CustomListDTO> getUserCustomLists(UUID userId) {
        List<CustomList> lists = customListRepository.findByUserId(userId);
        return lists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Adds a game to a specific custom list.
     *
     * @param userId the UUID of the user
     * @param listId the UUID of the custom list
     * @param gameId the UUID of the game to add
     */
    @Transactional
    public void addGameToList(UUID userId, UUID listId, UUID gameId) {
        CustomList customList = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom list not found with ID: " + listId));

        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));

        customList.getGames().add(game);
        customListRepository.save(customList);
    }

    /**
     * Removes a game from a specific custom list.
     *
     * @param userId the UUID of the user
     * @param listId the UUID of the custom list
     * @param gameId the UUID of the game to remove
     */
    @Transactional
    public void removeGameFromList(UUID userId, UUID listId, UUID gameId) {
        CustomList customList = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom list not found with ID: " + listId));

        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + gameId));

        if (!customList.getGames().contains(game)) {
            throw new ResourceNotFoundException("Game with ID: " + gameId + " is not in the list.");
        }

        customList.getGames().remove(game);
        customListRepository.save(customList);
    }

    /**
     * Deletes a custom list.
     *
     * @param userId the UUID of the user
     * @param listId the UUID of the custom list to delete
     */
    @Transactional
    public void deleteCustomList(UUID userId, UUID listId) {
        CustomList customList = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Custom list not found with ID: " + listId));

        // Prevent deletion of default list
        if (customList.isDefault()) {
            throw new IllegalArgumentException("Cannot delete the default list.");
        }

        customListRepository.delete(customList);
    }

    /**
     * Converts a CustomList entity to CustomListDTO.
     *
     * @param customList the CustomList entity
     * @return the corresponding CustomListDTO
     */
    private CustomListDTO convertToDTO(CustomList customList) {
        CustomListDTO dto = modelMapper.map(customList, CustomListDTO.class);
        Set<UUID> gameIds = customList.getGames().stream()
                .map(Games::getId)
                .collect(Collectors.toSet());
        dto.setGameIds(gameIds);
        return dto;
    }
}
