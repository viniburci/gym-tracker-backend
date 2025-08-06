 package com.burci.security.workout;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.burci.security.auth.AuthenticationService;
import com.burci.security.exercise.Exercise;
import com.burci.security.exercise.ExerciseRepository;
import com.burci.security.user.Role;
import com.burci.security.user.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;
    
    @Autowired
    private AuthenticationService authenticationService;

    @Transactional
    public List<WorkoutDTO> findAll() {
        User user = authenticationService.getAuthenticatedUser();

        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_" + Role.ADMIN.name()));


        if (!isAdmin) {
            throw new AccessDeniedException("Você não tem permissão para visualizar todos os treinos.");
        }

        return workoutRepository.findAll().stream()
                .map(WorkoutDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<WorkoutDTO> findAllByUser() {
    	User user = authenticationService.getAuthenticatedUser();
        List<Workout> workouts = workoutRepository.findAllByUser(user.getEmail());

        //Inicializa explicitamente as coleções para evitar problemas de Lazy Loading
        workouts.forEach(workout -> workout.getWorkoutExercises().size());

        return workouts.stream().map(WorkoutDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public WorkoutDTO findById(Long id) {
    	User user = authenticationService.getAuthenticatedUser();
        Workout workout = workoutRepository.findByIdAndUser(id, user.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado ou acesso negado"));
        return new WorkoutDTO(workout);
    }

    @Transactional
    public WorkoutDTO save(Workout workout) {
        User user = authenticationService.getAuthenticatedUser();
        
        List<WorkoutExercise> validatedExercises = workout.getWorkoutExercises().stream()
            .map(we -> {
                Exercise exercise = exerciseRepository.findById(we.getExercise().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado"));
                WorkoutExercise newWe = new WorkoutExercise();
                newWe.setExercise(exercise);
                newWe.setSets(we.getSets());
                newWe.setReps(we.getReps());
                newWe.setPosition(we.getPosition());
                newWe.setWorkout(workout);
                return newWe;
            })
            .collect(Collectors.toList());

        workout.setUser(user);
        workout.setWorkoutExercises(validatedExercises);
        Workout savedWorkout = workoutRepository.save(workout);
        return new WorkoutDTO(savedWorkout);
    }

    @Transactional
    public WorkoutDTO update(Long id, Workout updatedWorkout) {
    	User user = authenticationService.getAuthenticatedUser();
        Workout existingWorkout = workoutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        if (!existingWorkout.getUser().equals(user)) {
            throw new AccessDeniedException("Você não tem permissão para alterar este treino.");
        }

        existingWorkout.setName(updatedWorkout.getName());

        // Limpar a coleção existente
        existingWorkout.getWorkoutExercises().clear();

        // Adicionar os novos exercícios
        updatedWorkout.getWorkoutExercises().forEach(workoutExercise -> {
            Exercise exercise = exerciseRepository.findById(workoutExercise.getExercise().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado"));

            WorkoutExercise newWorkoutExercise = new WorkoutExercise(
                existingWorkout, 
                exercise, 
                workoutExercise.getSets(), 
                workoutExercise.getReps(), 
                workoutExercise.getPosition()
            );
            
            existingWorkout.getWorkoutExercises().add(newWorkoutExercise);
        });

        return new WorkoutDTO(workoutRepository.save(existingWorkout));
    }

    @Transactional
    public void delete(Long id) {
    	User user = authenticationService.getAuthenticatedUser();
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        if (!workout.getUser().equals(user)) {
            throw new AccessDeniedException("Você não tem permissão para deletar este treino.");
        }

        //Deletar os exercícios vinculados antes de deletar o treino
        workoutExerciseRepository.deleteAll(workout.getWorkoutExercises());
        workoutRepository.delete(workout);
    }

    @Transactional
    public WorkoutExerciseDTO addExerciseToWorkout(Long workoutId, Long exerciseId, int sets, int reps, int position) {
    	User user = authenticationService.getAuthenticatedUser();
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        if (!workout.getUser().equals(user)) {
            throw new AccessDeniedException("Você não tem permissão para modificar este treino.");
        }

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado"));

        Optional<WorkoutExercise> existingWorkoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getExercise().getId().equals(exerciseId))
                .findFirst();

        if (existingWorkoutExercise.isPresent()) {
            WorkoutExercise workoutExercise = existingWorkoutExercise.get();
            workoutExercise.setSets(sets);
            workoutExercise.setReps(reps);
            return new WorkoutExerciseDTO(workoutExerciseRepository.save(workoutExercise));
        }

        WorkoutExercise newWorkoutExercise = new WorkoutExercise(workout, exercise, sets, reps, position);
        return new WorkoutExerciseDTO(workoutExerciseRepository.save(newWorkoutExercise));
    }
}