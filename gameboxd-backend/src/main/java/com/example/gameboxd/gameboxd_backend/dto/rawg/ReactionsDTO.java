// Path: dto/rawg/ReactionsDTO.java

package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO representing Reactions details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReactionsDTO {
    @JsonProperty("reallylove")
    private int reallyLove;

    @JsonProperty("love")
    private int love;

    @JsonProperty("neutral")
    private int neutral;

    @JsonProperty("hate")
    private int hate;

    @JsonProperty("reallyhate")
    private int reallyHate;
}
