package com.example.gameboxd.gameboxd_backend.config;

import com.example.gameboxd.gameboxd_backend.dto.game.GameResponseDTO;
import com.example.gameboxd.gameboxd_backend.dto.users.UserProfileDTO;
import com.example.gameboxd.gameboxd_backend.model.Games;
import com.example.gameboxd.gameboxd_backend.model.User;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class AppConfig {

    private final RawgApiProperties rawgApiProperties;

    public AppConfig(RawgApiProperties rawgApiProperties) {
        this.rawgApiProperties = rawgApiProperties;
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(rawgApiProperties.getTimeout());
        factory.setReadTimeout(rawgApiProperties.getTimeout());
        return new RestTemplate(factory);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
    
        // Map User to UserProfileDTO
        modelMapper.createTypeMap(User.class, UserProfileDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(UserProfileDTO::setFollowersCount);
                    mapper.skip(UserProfileDTO::setFollowingCount);
                    mapper.skip(UserProfileDTO::setReviewedGames);

                });
    
        // Map Games to GameResponseDTO
        modelMapper.createTypeMap(Games.class, GameResponseDTO.class);
    
        return modelMapper;
    }
    

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("games", "genres", "platforms");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES));
        return cacheManager;
    }
}
