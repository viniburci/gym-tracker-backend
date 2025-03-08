package com.burci.security.exercise;

import lombok.Data;

@Data
public class ExerciseDTO {
    private Long id;
    private String name;
    private String type;
    private String imageUrl;
    
    public ExerciseDTO() {}
    
    public ExerciseDTO(Exercise exercise) {
    	id = exercise.getId();
    	name = exercise.getName();
    	type = exercise.getType().toString();
    	imageUrl = "/exercises/" + exercise.getId() + "/image";
    }
}