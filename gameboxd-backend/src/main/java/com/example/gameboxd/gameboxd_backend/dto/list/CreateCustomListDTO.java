package com.example.gameboxd.gameboxd_backend.dto.list;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCustomListDTO {
    @NotBlank(message = "List name is mandatory")
    private String name;
}
