// Path: dto/rawg/MetacriticPlatformDTO.java

package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO representing Metacritic Platform details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetacriticPlatformDTO {
    private PlatformInfo platform;
    private int score;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlatformInfo {
        private int id;
        private String name;
        private String slug;
    }
}
