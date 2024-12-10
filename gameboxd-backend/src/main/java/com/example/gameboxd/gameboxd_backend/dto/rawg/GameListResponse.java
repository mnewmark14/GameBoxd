package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.example.gameboxd.gameboxd_backend.dto.game.GameDTO;
import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO representing the list response for games from the RAWG API.
 */
@Data
public class GameListResponse {
    private int count;
    
    @JsonProperty("next")
    private String next;
    
    @JsonProperty("previous")
    private String previous;
    
    @JsonProperty("results")
    private List<GameDTO> results;
}
