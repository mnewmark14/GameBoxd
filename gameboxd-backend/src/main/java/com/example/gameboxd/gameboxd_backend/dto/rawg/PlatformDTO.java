package com.example.gameboxd.gameboxd_backend.dto.rawg;

import lombok.Data;

@Data
public class PlatformDTO {
    private Platform platform;
    
    @Data
    public static class Platform {
        private int id;
        private String name;
        private String slug;
        private String imageBackground;
    }
}
