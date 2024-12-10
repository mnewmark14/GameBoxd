package com.example.gameboxd.gameboxd_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.gameboxd.gameboxd_backend.config.RawgApiProperties;

@SpringBootApplication
@EnableConfigurationProperties(RawgApiProperties.class)
public class GameboxdBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameboxdBackendApplication.class, args);
    }

}
