package com.burci.security.exercise;

import lombok.Data;

@Data
public class ExerciseDTO {
    private Long id;
    private String name;
    private String type;
    private String imageUrl;
}