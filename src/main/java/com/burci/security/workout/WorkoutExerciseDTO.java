package com.burci.security.workout;

import com.burci.security.exercise.Exercise;

import lombok.Data;

@Data
public class WorkoutExerciseDTO {
    private Long id;
//    private Long exerciseId;
//    private String exerciseName;
//    private String imageUrl;
    private Exercise exercise;
    private int sets;
    private int reps;
    
    public WorkoutExerciseDTO() { }
    
    public WorkoutExerciseDTO(WorkoutExercise workoutExercise) {
    	id = workoutExercise.getId();
//    	exerciseId = workoutExercise.getExercise().getId();
//    	exerciseName = workoutExercise.getExercise().getName();
//    	
//    	String imageUrl = "/exercises/" + workoutExercise.getExercise().getId() + "/image";
//    	this.imageUrl = imageUrl;
    	exercise = workoutExercise.getExercise();
    	
    	sets = workoutExercise.getSets();
    	reps = workoutExercise.getReps();
    }
}