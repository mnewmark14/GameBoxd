package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GenreDTO {
    private int id;
    private String name;
    private String slug;

    @JsonProperty("games_count")
    private int gamesCount;

    @JsonProperty("image_background")
    private String imageBackground;

    private String description;

}
