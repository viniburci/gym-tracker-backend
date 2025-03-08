package com.burci.security.workout;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.burci.security.exercise.Exercise;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long>{

	Optional<WorkoutExercise> findByWorkoutAndExercise(Workout workout, Exercise exercise);
	
}
