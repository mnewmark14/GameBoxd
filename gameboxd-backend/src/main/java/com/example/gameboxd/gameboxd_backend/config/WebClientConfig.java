// src/main/java/com/example/gameboxd/gameboxd_backend/config/WebClientConfig.java

package com.example.gameboxd.gameboxd_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient rawgWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.rawg.io/api")
                .build();
    }
}
