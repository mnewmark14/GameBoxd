package com.example.gameboxd.gameboxd_backend.dto.developer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Developer from the RAWG API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperDTO {
    private int id;
    private String name;
    private String slug;

    @JsonProperty("games_count")
    private int gamesCount;

    @JsonProperty("image_background")
    private String imageBackground;

    private String description;


}
