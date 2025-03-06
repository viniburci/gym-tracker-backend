package com.burci.security.workout;

import java.util.List;

import lombok.Data;

@Data
public class WorkoutDTO {
    private Long id;
    private String name;
    private List<WorkoutExerciseDTO> workoutExercises;
}