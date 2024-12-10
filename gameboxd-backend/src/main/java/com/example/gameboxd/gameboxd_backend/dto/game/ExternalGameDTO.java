// src/main/java/com/example/gameboxd/gameboxd_backend/dto/rawg/ExternalGameDTO.java
package com.example.gameboxd.gameboxd_backend.dto.game;

import com.example.gameboxd.gameboxd_backend.dto.rawg.EsrbRatingDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.GenreDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.PlatformDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class ExternalGameDTO {
    private int id;
    private String slug;
    private String name;
    private LocalDate released;

    @JsonProperty("background_image")
    private String backgroundImage;

    private double rating;

    @JsonProperty("description_raw")
    private String descriptionRaw;

    @JsonProperty("rating_top")
    private int ratingTop;

    private List<Map<String, Object>> ratings;

    @JsonProperty("ratings_count")
    private int ratingsCount;

    @JsonProperty("reviews_text_count")
    private String reviewsTextCount;

    private int added;

    @JsonProperty("added_by_status")
    private Map<String, Object> addedByStatus;

    private int metacritic;
    private int playtime;

    @JsonProperty("suggestions_count")
    private int suggestionsCount;

    private String updated;

    @JsonProperty("esrb_rating")
    private EsrbRatingDTO esrbRating;

    private List<PlatformDTO> platforms;


}
