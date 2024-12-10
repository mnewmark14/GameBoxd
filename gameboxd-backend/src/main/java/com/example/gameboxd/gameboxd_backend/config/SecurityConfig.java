package com.example.gameboxd.gameboxd_backend.config;

import com.example.gameboxd.gameboxd_backend.security.CustomAuthenticationEntryPoint;
import com.example.gameboxd.gameboxd_backend.security.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer; // Added import
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Added imports for CORS
import org.springframework.web.cors.CorsConfiguration; 
import org.springframework.web.cors.CorsConfigurationSource; 
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; 
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS
            .cors(Customizer.withDefaults())
            // Disable CSRF as we're using token-based authentication
            .csrf(csrf -> csrf.disable())
            // Handle unauthorized access attempts
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler))
            // Set session management to stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Define URL authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/api/v1/users/register",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/ratings/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/ratings/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/ratings/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/followers/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/followers/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()
                // Permit unauthenticated GET requests to /api/v1/games/**
                .requestMatchers(HttpMethod.GET, "/api/v1/games/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/games/*/ratings/summary").permitAll()
                // Require authentication for other HTTP methods on /api/v1/games/**
                .requestMatchers(HttpMethod.POST, "/api/v1/games/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/games/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/games/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/games/*/reviews").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/games/*/log").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/games/*/log").authenticated()

                .anyRequest().authenticated()
            );

        // Add the TokenAuthenticationFilter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your frontend application
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow headers required for your application
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Allow credentials (e.g., cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Apply the configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
