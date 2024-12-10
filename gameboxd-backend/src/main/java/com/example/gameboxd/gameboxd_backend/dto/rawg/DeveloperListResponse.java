package com.example.gameboxd.gameboxd_backend.dto.rawg;

import com.example.gameboxd.gameboxd_backend.dto.developer.DeveloperDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO representing the list response for developers from the RAWG API.
 */
@Data
public class DeveloperListResponse {
    private int count;
    
    @JsonProperty("next")
    private String next;
    
    @JsonProperty("previous")
    private String previous;
    
    @JsonProperty("results")
    private List<DeveloperDTO> results;
}
