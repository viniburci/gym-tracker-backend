package com.burci.security.workout;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.burci.security.auth.AuthenticationService;
import com.burci.security.exercise.Exercise;
import com.burci.security.exercise.ExerciseRepository;

@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;
    
    @Autowired
    private AuthenticationService authenticationService;

    public List<Workout> findAll() {
        return workoutRepository.findAll();
    }

    public List<Workout> findAllByUser() {
        String username = authenticationService.getAuthenticatedUsername();
        return workoutRepository.findAllByUser(username);
    }

    public Workout findById(Long id) {
        String username = authenticationService.getAuthenticatedUsername();
        return workoutRepository.findByIdAndUser(id, username)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado ou acesso negado"));
    }

    public Workout save(Workout workout) {
        return workoutRepository.save(workout);
    }

    public Workout update(Long id, Workout updatedWorkout) {
        return workoutRepository.findById(id).map(existingWorkout -> {
            existingWorkout.setName(updatedWorkout.getName());

            List<WorkoutExercise> validExercises = updatedWorkout.getWorkoutExercises().stream()
                .map(workoutExercise -> {

                    Exercise exercise = exerciseRepository.findById(workoutExercise.getExercise().getId())
                        .orElseThrow(() -> new RuntimeException("Exercício com ID " + workoutExercise.getExercise().getId() + " não encontrado"));

                    return new WorkoutExercise(existingWorkout, exercise, workoutExercise.getSets(), workoutExercise.getReps());
                })
                .collect(Collectors.toList());

            existingWorkout.setWorkoutExercises(validExercises);
            return workoutRepository.save(existingWorkout);
        }).orElseThrow(() -> new RuntimeException("Treino não encontrado"));
    }


    public void delete(Long id) {
        workoutRepository.deleteById(id);
    }

    public WorkoutExercise addExerciseToWorkout(Long workoutId, Long exerciseId, int sets, int reps) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado"));
        
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado"));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setSets(sets);
        workoutExercise.setReps(reps);

        return workoutExerciseRepository.save(workoutExercise);
    }
    

}