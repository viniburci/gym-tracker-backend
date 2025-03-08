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
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(service.findById(id, principal));
    }
    
    @GetMapping("/mine")
    public ResponseEntity<List<WorkoutDTO>> findAllByUser(Principal principal){
    	return ResponseEntity.ok(service.findAllByUser(principal));
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout, Principal principal) {
        return ResponseEntity.ok(service.save(workout, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDTO> updateWorkout(@PathVariable Long id, @RequestBody Workout workout, Principal principal) {
        return ResponseEntity.ok(service.update(id, workout, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id, Principal principal) {
        service.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workoutId}/addExercise/{exerciseId}")
    public ResponseEntity<WorkoutExerciseDTO> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @PathVariable Long exerciseId,
            @RequestParam int sets,
            @RequestParam int reps,
            Principal principal) {
        
        WorkoutExerciseDTO workoutExerciseDTO = service.addExerciseToWorkout(workoutId, exerciseId, sets, reps, principal);
        return ResponseEntity.ok(workoutExerciseDTO);
    }
}