package com.example.gameboxd.gameboxd_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//mport com.example.gameboxd.gameboxd_backend.converter.StringListConverter;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Games {

    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "rawg_id", nullable = false, unique = true)
    private Integer rawgId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    // @Convert(converter = StringListConverter.class)
    // @Column(name = "genres", columnDefinition = "jsonb")
    // private List<String> genres;

    // @Convert(converter = StringListConverter.class)
    // @Column(name = "platforms", columnDefinition = "jsonb")
    // private List<String> platforms;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "average_rating", nullable = false)
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(name = "total_ratings", nullable = false)
    @Builder.Default
    private Integer totalRatings = 0;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameLog> gameLogs = new ArrayList<>();

    // Add setters if not present
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }


    // Relationships can be added here later (e.g., reviews, ratings)
    @ManyToMany(mappedBy = "games")
    @Builder.Default
    private Set<CustomList> customLists = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reviews> reviews = new ArrayList<>();



}
