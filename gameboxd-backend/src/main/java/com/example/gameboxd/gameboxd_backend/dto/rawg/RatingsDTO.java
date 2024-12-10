// Path: dto/rawg/RatingsDTO.java

package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * DTO representing Ratings details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingsDTO {
    private List<RatingDetail> ratings;
    private int count;
    private double average;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RatingDetail {
        private int id;
        private String title;
        private double percent;
    }
}
