// GameListResponseDTO.java
package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class GameListResponseDTO {
    private int count;
    private String next;
    private String previous;
    private List<GameResponseDTO> results;
}
