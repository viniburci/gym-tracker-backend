package com.burci.security.workout;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class WorkoutDTO {
    private Long id;
    private String name;
    private List<WorkoutExerciseDTO> workoutExercises;
    
    public WorkoutDTO() {}
    
    public WorkoutDTO(Workout workout) {
        id = workout.getId();
        name = workout.getName();
        workoutExercises = workout.getWorkoutExercises().stream()
                .map(WorkoutExerciseDTO::new)
                .collect(Collectors.toList());
    }
}