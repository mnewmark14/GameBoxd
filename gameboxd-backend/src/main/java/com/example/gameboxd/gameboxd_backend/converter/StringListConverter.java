// // src/main/java/com/example/gameboxd/gameboxd_backend/converter/ListToJsonConverter.java
// package com.example.gameboxd.gameboxd_backend.converter;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import jakarta.persistence.AttributeConverter;
// import jakarta.persistence.Converter;

// import java.io.IOException;
// import java.util.List;

// @Converter(autoApply = true)
// public class StringListConverter implements AttributeConverter<List<String>, String> {

//     private final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public String convertToDatabaseColumn(List<String> list) {
//         if (list == null || list.isEmpty()) {
//             return "[]";
//         }
//         try {
//             return objectMapper.writeValueAsString(list);
//         } catch (JsonProcessingException e) {
//             throw new IllegalArgumentException("Error converting list to JSON", e);
//         }
//     }

//     @Override
//     public List<String> convertToEntityAttribute(String json) {
//         if (json == null || json.trim().isEmpty()) {
//             return List.of();
//         }
//         try {
//             return objectMapper.readValue(json, List.class);
//         } catch (IOException e) {
//             throw new IllegalArgumentException("Error converting JSON to list", e);
//         }
//     }
// }
