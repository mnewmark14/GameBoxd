package com.example.gameboxd.gameboxd_backend.dto.rawg;

import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class GenreListResponse {
    private int count;
    
    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("results")
    private List<GenreDTO> results;
}
