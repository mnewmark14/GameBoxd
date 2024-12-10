package com.example.gameboxd.gameboxd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "custom_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    // Indicates if this is a default list (e.g., "Reviewed Games")
    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many-to-Many relationship with Games
    @ManyToMany
    @JoinTable(
        name = "custom_list_games",
        joinColumns = @JoinColumn(name = "custom_list_id"),
        inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    @Builder.Default
    private Set<Games> games = new HashSet<>();
}
