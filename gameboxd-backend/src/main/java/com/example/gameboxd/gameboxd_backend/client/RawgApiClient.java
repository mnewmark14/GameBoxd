package com.example.gameboxd.gameboxd_backend.client;

import com.example.gameboxd.gameboxd_backend.config.RawgApiProperties;
import com.example.gameboxd.gameboxd_backend.dto.developer.DeveloperDTO;
import com.example.gameboxd.gameboxd_backend.dto.game.ExternalGameDTO;
import com.example.gameboxd.gameboxd_backend.dto.game.GameDTO;
import com.example.gameboxd.gameboxd_backend.dto.rawg.*;
import com.example.gameboxd.gameboxd_backend.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RawgApiClient {

    private static final Logger logger = LoggerFactory.getLogger(RawgApiClient.class);

    private final RestTemplate restTemplate;
    private final RawgApiProperties rawgApiProperties;

    private final WebClient webClient;

    @Value("${rawg.api.key}")
    private String apiKey;


    /**
     * Fetches a list of developers from the RAWG API.
     *
     * @param page     the page number
     * @param pageSize the number of results per page
     * @return list of DeveloperDTO
     */
    public List<DeveloperDTO> getDevelopers(int page, int pageSize) {
        String url = String.format("%s/developers?page=%d&page_size=%d&key=%s",
                rawgApiProperties.getBaseUrl(),
                page,
                pageSize,
                rawgApiProperties.getKey());
        try {
            logger.info("Fetching developers from RAWG API: {}", url);
            DeveloperListResponse response = restTemplate.getForObject(url, DeveloperListResponse.class);
            if (response != null) {
                return response.getResults();
            } else {
                throw new ExternalApiException("RAWG API returned null response for developers.");
            }
        } catch (RestClientException e) {
            logger.error("Error fetching developers from RAWG API", e);
            throw new ExternalApiException("Failed to fetch developers from RAWG API.");
        }
    }

    /**
     * Fetches game details by RAWG ID.
     *
     * @param rawgId the RAWG ID of the game
     * @return Mono<ExternalGameDTO>
     */
    public Mono<ExternalGameDTO> getGameByRawgId(int rawgId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{id}")
                        .queryParam("key", apiKey)
                        .build(rawgId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    // Handle 4xx errors
                    return Mono.error(new ExternalApiException("Client error when fetching game details from RAWG API."));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    // Handle 5xx errors
                    return Mono.error(new ExternalApiException("Server error when fetching game details from RAWG API."));
                })
                .bodyToMono(ExternalGameDTO.class);
    }

    /**
     * Fetches a list of games from RAWG API based on search parameters.
     *
     * @param page     the page number
     * @param pageSize the number of results per page
     * @param search   the search query
     * @return list of GameDTO
     */
    public List<GameDTO> searchGames(int page, int pageSize, String search) {
        String url = String.format("%s/games?page=%d&page_size=%d&search=%s&key=%s",
                rawgApiProperties.getBaseUrl(),
                page,
                pageSize,
                search,
                rawgApiProperties.getKey());
        try {
            logger.info("Searching games from RAWG API: {}", url);
            GameListResponse response = restTemplate.getForObject(url, GameListResponse.class);
            if (response != null) {
                return response.getResults();
            } else {
                throw new ExternalApiException("RAWG API returned null response for game search.");
            }
        } catch (RestClientException e) {
            logger.error("Error searching games from RAWG API", e);
            throw new ExternalApiException("Failed to search games from RAWG API.");
        }
    }

    /**
     * Fetches a list of genres from the RAWG API.
     *
     * @param page     the page number
     * @param pageSize the number of results per page
     * @return list of GenreDTO
     */
    public List<GenreDTO> getGenres(int page, int pageSize) {
        String url = String.format("%s/genres?page=%d&page_size=%d&key=%s",
                rawgApiProperties.getBaseUrl(),
                page,
                pageSize,
                rawgApiProperties.getKey());
        try {
            logger.info("Fetching genres from RAWG API: {}", url);
            GenreListResponse response = restTemplate.getForObject(url, GenreListResponse.class);
            if (response != null) {
                return response.getResults();
            } else {
                throw new ExternalApiException("RAWG API returned null response for genres.");
            }
        } catch (RestClientException e) {
            logger.error("Error fetching genres from RAWG API", e);
            throw new ExternalApiException("Failed to fetch genres from RAWG API.");
        }
    }

    /**
     * Fetches a list of platforms from the RAWG API.
     *
     * @param page     the page number
     * @param pageSize the number of results per page
     * @return list of PlatformDTO
     */
    public List<PlatformDTO> getPlatforms(int page, int pageSize) {
        String url = String.format("%s/platforms?page=%d&page_size=%d&key=%s",
                rawgApiProperties.getBaseUrl(),
                page,
                pageSize,
                rawgApiProperties.getKey());
        try {
            logger.info("Fetching platforms from RAWG API: {}", url);
            PlatformListResponse response = restTemplate.getForObject(url, PlatformListResponse.class);
            if (response != null) {
                return response.getResults();
            } else {
                throw new ExternalApiException("RAWG API returned null response for platforms.");
            }
        } catch (RestClientException e) {
            logger.error("Error fetching platforms from RAWG API", e);
            throw new ExternalApiException("Failed to fetch platforms from RAWG API.");
        }
    }

    // Additional methods for other RAWG API endpoints as needed
}
