package com.example.gameboxd.gameboxd_backend.dto.list;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CustomListDTO {
    private UUID id;
    private String name;
    private boolean isDefault;
    private Set<UUID> gameIds;
}
