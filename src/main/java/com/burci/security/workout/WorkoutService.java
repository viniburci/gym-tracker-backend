package com.burci.security.workout;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.burci.security.auth.AuthenticationService;
import com.burci.security.exercise.Exercise;
import com.burci.security.exercise.ExerciseRepository;
import com.burci.security.user.User;
import com.burci.security.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<WorkoutDTO> findAll() {
        List<Workout> workouts = workoutRepository.findAll();

        return workouts.stream().map(workout -> {
            WorkoutDTO workoutDTO = new WorkoutDTO();
            workoutDTO.setId(workout.getId());
            workoutDTO.setName(workout.getName());

            workoutDTO.setWorkoutExercises(workout.getWorkoutExercises().stream()
                .map(workoutExercise -> {
                    WorkoutExerciseDTO workoutExerciseDTO = new WorkoutExerciseDTO();
                    workoutExerciseDTO.setId(workoutExercise.getId());
                    workoutExerciseDTO.setExerciseId(workoutExercise.getExercise().getId());
                    workoutExerciseDTO.setExerciseName(workoutExercise.getExercise().getName());
                    workoutExerciseDTO.setSets(workoutExercise.getSets());
                    workoutExerciseDTO.setReps(workoutExercise.getReps());

                    String imageUrl = "/exercises/" + workoutExercise.getExercise().getId() + "/image";
                    workoutExerciseDTO.setImageUrl(imageUrl);

                    return workoutExerciseDTO;
                })
                .collect(Collectors.toList()));

            return workoutDTO;
        })
        .collect(Collectors.toList());
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

    public Workout save(Workout workout, Principal principal) {
        List<WorkoutExercise> validatedExercises = workout.getWorkoutExercises().stream().map(we -> {
            Exercise exercise = exerciseRepository.findById(we.getExercise().getId())
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado: " + we.getExercise().getId()));
            return new WorkoutExercise(workout, exercise, we.getSets(), we.getReps());
        }).collect(Collectors.toList());
        
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        workout.setUser(user);
        workout.setWorkoutExercises(validatedExercises);
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