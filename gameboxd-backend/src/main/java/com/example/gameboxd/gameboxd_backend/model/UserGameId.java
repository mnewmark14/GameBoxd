package com.example.gameboxd.gameboxd_backend.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserGameId implements Serializable {
    private UUID userId;
    private UUID gameId;
}
