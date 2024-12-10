package com.example.gameboxd.gameboxd_backend.controller;

import com.example.gameboxd.gameboxd_backend.dto.list.CustomListDTO;
import com.example.gameboxd.gameboxd_backend.dto.list.CreateCustomListDTO;
import com.example.gameboxd.gameboxd_backend.service.CustomListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/custom-lists")
@RequiredArgsConstructor
public class CustomListController {

    private final CustomListService customListService;

    /**
     * Endpoint to create a new custom list.
     *
     * @param createCustomListDTO the DTO containing list details
     * @param authentication      the authentication object containing the current user's details
     * @return ResponseEntity with the created CustomListDTO
     */
    @PostMapping
    public ResponseEntity<CustomListDTO> createCustomList(@Valid @RequestBody CreateCustomListDTO createCustomListDTO,
                                                          Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        CustomListDTO customListDTO = customListService.createCustomList(userId, createCustomListDTO);
        return new ResponseEntity<>(customListDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all custom lists of the authenticated user.
     *
     * @param authentication the authentication object containing the current user's details
     * @return ResponseEntity with a list of CustomListDTO
     */
    @GetMapping
    public ResponseEntity<List<CustomListDTO>> getUserCustomLists(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        List<CustomListDTO> customLists = customListService.getUserCustomLists(userId);
        return ResponseEntity.ok(customLists);
    }

    /**
     * Endpoint to add a game to a specific custom list.
     *
     * @param listId          the UUID of the custom list
     * @param gameId          the UUID of the game to add
     * @param authentication  the authentication object containing the current user's details
     * @return ResponseEntity with status OK
     */
    @PostMapping("/{listId}/games/{gameId}")
    public ResponseEntity<Void> addGameToList(@PathVariable UUID listId,
                                             @PathVariable UUID gameId,
                                             Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        customListService.addGameToList(userId, listId, gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to remove a game from a specific custom list.
     *
     * @param listId          the UUID of the custom list
     * @param gameId          the UUID of the game to remove
     * @param authentication  the authentication object containing the current user's details
     * @return ResponseEntity with status OK
     */
    @DeleteMapping("/{listId}/games/{gameId}")
    public ResponseEntity<Void> removeGameFromList(@PathVariable UUID listId,
                                                  @PathVariable UUID gameId,
                                                  Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        customListService.removeGameFromList(userId, listId, gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to delete a custom list.
     *
     * @param listId          the UUID of the custom list to delete
     * @param authentication  the authentication object containing the current user's details
     * @return ResponseEntity with status NO_CONTENT
     */
    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteCustomList(@PathVariable UUID listId,
                                                Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        customListService.deleteCustomList(userId, listId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Utility method to extract the current authenticated user's UUID.
     *
     * @param authentication the authentication object
     * @return UUID of the current user
     */
    private UUID getCurrentUserId(Authentication authentication) {
        return (UUID) authentication.getPrincipal();
    }
}
