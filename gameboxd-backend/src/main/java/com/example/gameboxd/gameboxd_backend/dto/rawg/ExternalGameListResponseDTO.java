// src/main/java/com/example/gameboxd/gameboxd_backend/dto/rawg/ExternalGameListResponseDTO.java
package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.example.gameboxd.gameboxd_backend.dto.game.ExternalGameDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExternalGameListResponseDTO {
    private int count;

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("results")
    private List<ExternalGameDTO> results;
}
