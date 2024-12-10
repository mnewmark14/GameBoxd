// Path: dto/rawg/EsrbRatingDTO.java

package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO representing ESRB Rating details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsrbRatingDTO {
    private int id;
    private String slug;
    private String name;

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty("name_ru")
    private String nameRu;
}
