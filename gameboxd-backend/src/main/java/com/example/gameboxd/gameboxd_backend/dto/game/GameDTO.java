package com.example.gameboxd.gameboxd_backend.dto.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.gameboxd.gameboxd_backend.dto.rawg.AddedByStatusDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.EsrbRatingDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.GenreDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.MetacriticPlatformDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.PlatformDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.RatingsDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.ReactionsDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a Game from the RAWG API with only the necessary fields.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {
    private int id;
    private String slug;
    private String name;

    @JsonProperty("name_original")
    private String nameOriginal;

    private String description;

    private int metacritic;

    @JsonProperty("metacritic_platforms")
    private List<MetacriticPlatformDTO> metacriticPlatforms;

    private LocalDate released;

    private boolean tba;

    private LocalDateTime updated;

    @JsonProperty("background_image")
    private String backgroundImage;

    @JsonProperty("background_image_additional")
    private String backgroundImageAdditional;

    private String website;

    private double rating; // Changed to double for decimal values

    @JsonProperty("rating_top")
    private int ratingTop;

    private RatingsDTO ratings;

    private ReactionsDTO reactions;

    private int added;

    @JsonProperty("added_by_status")
    private AddedByStatusDTO addedByStatus;

    private int playtime;

    @JsonProperty("screenshots_count")
    private int screenshotsCount;

    private int moviesCount;

    private int creatorsCount;

    private int achievementsCount;

    @JsonProperty("parent_achievements_count")
    private int parentAchievementsCount;

    @JsonProperty("reddit_url")
    private String redditUrl;

    @JsonProperty("reddit_name")
    private String redditName;

    @JsonProperty("reddit_description")
    private String redditDescription;

    @JsonProperty("reddit_logo")
    private String redditLogo;

    @JsonProperty("reddit_count")
    private int redditCount;

    @JsonProperty("twitch_count")
    private int twitchCount;

    @JsonProperty("youtube_count")
    private int youtubeCount;

    @JsonProperty("reviews_text_count")
    private int reviewsTextCount;

    @JsonProperty("ratings_count")
    private int ratingsCount;

    @JsonProperty("suggestions_count")
    private int suggestionsCount;

    @JsonProperty("alternative_names")
    private List<String> alternativeNames;

    @JsonProperty("metacritic_url")
    private String metacriticUrl;

    @JsonProperty("parents_count")
    private int parentsCount;

    @JsonProperty("additions_count")
    private int additionsCount;

    @JsonProperty("game_series_count")
    private int gameSeriesCount;

    @JsonProperty("esrb_rating")
    private EsrbRatingDTO esrbRating;

    private List<PlatformDTO> platforms;

    private List<GenreDTO> genres;

}