package com.burci.security.workout;

import lombok.Data;

@Data
public class WorkoutExerciseDTO {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private String imageUrl;
    private int sets;
    private int reps;
}