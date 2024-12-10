package com.example.gameboxd.gameboxd_backend.dto.rawg;

import lombok.Data;

import java.util.List;

@Data
public class PlatformListResponse {
    private int count;
    private String next;
    private String previous;
    private List<PlatformDTO> results;
}
