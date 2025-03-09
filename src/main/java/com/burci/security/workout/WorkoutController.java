package com.burci.security.workout;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService service;

    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getAllWorkouts() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO> getWorkoutById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/mine")
    public ResponseEntity<List<WorkoutDTO>> findAllByUser(){
    	return ResponseEntity.ok(service.findAllByUser());
    }

    @PostMapping
    public ResponseEntity<WorkoutDTO> createWorkout(@RequestBody Workout workout) {
        return ResponseEntity.ok(service.save(workout));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDTO> updateWorkout(@PathVariable Long id, @RequestBody Workout workout) {
        return ResponseEntity.ok(service.update(id, workout));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workoutId}/addExercise/{exerciseId}")
    public ResponseEntity<WorkoutExerciseDTO> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @RequestParam int sets,
            @RequestParam int reps) {
        
        WorkoutExerciseDTO workoutExerciseDTO = service.addExerciseToWorkout(workoutId, exerciseId, sets, reps);
        return ResponseEntity.ok(workoutExerciseDTO);
    }
}