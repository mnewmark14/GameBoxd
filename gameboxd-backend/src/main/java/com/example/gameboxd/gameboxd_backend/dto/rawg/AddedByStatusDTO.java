// Path: dto/rawg/AddedByStatusDTO.java

package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO representing Added By Status details.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddedByStatusDTO {
    private int yet;
    private int owned;
    private int beaten;
    private int toplay;
    private int dropped;
    private int playing;
}
