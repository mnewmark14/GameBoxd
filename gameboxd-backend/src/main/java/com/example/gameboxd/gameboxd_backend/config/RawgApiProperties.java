package com.example.gameboxd.gameboxd_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rawg.api")
public class RawgApiProperties {
    private String baseUrl;
    private String key;
    private int timeout; // in milliseconds
}
