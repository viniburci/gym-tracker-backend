package com.burci.security.exercise;

import java.util.List;

import com.burci.security.workout.WorkoutExercise;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Exercise {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Lob
    private byte[] image;
    
    @OneToMany(mappedBy = "exercise")
    private List<WorkoutExercise> workoutExercises;
}
