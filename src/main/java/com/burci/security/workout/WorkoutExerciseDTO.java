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
    
    public WorkoutExerciseDTO() { }
    
    public WorkoutExerciseDTO(WorkoutExercise workoutExercise) {
    	id = workoutExercise.getId();
    	exerciseId = workoutExercise.getExercise().getId();
    	exerciseName = workoutExercise.getExercise().getName();
    	
    	String imageUrl = "/exercises/" + workoutExercise.getExercise().getId() + "/image";
    	this.imageUrl = imageUrl;
    	
    	sets = workoutExercise.getSets();
    	reps = workoutExercise.getReps();
    }
}