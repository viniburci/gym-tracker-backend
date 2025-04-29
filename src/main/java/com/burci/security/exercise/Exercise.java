package com.burci.security.exercise;

import java.util.List;

import com.burci.security.workout.WorkoutExercise;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Exercise {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Lob
    @JsonIgnore
    private byte[] image;
    
    @OneToMany(mappedBy = "exercise")
    @JsonIgnore
    private List<WorkoutExercise> workoutExercises;
}