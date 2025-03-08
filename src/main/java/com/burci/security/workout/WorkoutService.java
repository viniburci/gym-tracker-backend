package com.burci.security.workout;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    @Transactional
    public List<WorkoutDTO> findAllByUser(Principal principal) {
        User user = authenticationService.getAuthenticatedUser(principal);
        
        List<Workout> workouts = workoutRepository.findAllByUser(user.getEmail());
        
        //força o Hibernate a inicializar a coleção antes que a sessão seja fechada.
        workouts.forEach(workout -> workout.getWorkoutExercises().size());

        return workouts.stream()
                .map(WorkoutDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Workout findById(Long id, Principal principal) {
        User user = authenticationService.getAuthenticatedUser(principal);
        return workoutRepository.findByIdAndUser(id, user.getEmail())
                .orElseThrow(() -> new RuntimeException("Treino não encontrado ou acesso negado"));
    }

    @Transactional
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

    @Transactional
    public WorkoutDTO update(Long id, Workout updatedWorkout, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Buscar o treino e garantir que pertence ao usuário autenticado
        Workout existingWorkout = workoutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        if (!existingWorkout.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para alterar este treino.");
        }

        // Atualizar nome do treino
        existingWorkout.setName(updatedWorkout.getName());

        // Criar um mapa dos exercícios já vinculados ao treino
        Map<Long, WorkoutExercise> existingExercisesMap = existingWorkout.getWorkoutExercises().stream()
            .collect(Collectors.toMap(we -> we.getExercise().getId(), we -> we));

        // Criar um conjunto com os IDs dos exercícios enviados na requisição
        Set<Long> updatedExerciseIds = updatedWorkout.getWorkoutExercises().stream()
            .map(we -> we.getExercise().getId())
            .collect(Collectors.toSet());

        // Atualizar ou adicionar novos exercícios
        List<WorkoutExercise> updatedExercises = updatedWorkout.getWorkoutExercises().stream()
            .map(workoutExercise -> {
                Long exerciseId = workoutExercise.getExercise().getId();

                // Buscar exercício no banco (evita criar um novo)
                Exercise existingExercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new EntityNotFoundException("Exercício com ID " + exerciseId + " não encontrado."));

                // Se o exercício já existe no treino, apenas atualiza sets e reps
                if (existingExercisesMap.containsKey(exerciseId)) {
                    WorkoutExercise existingWorkoutExercise = existingExercisesMap.get(exerciseId);
                    existingWorkoutExercise.setSets(workoutExercise.getSets());
                    existingWorkoutExercise.setReps(workoutExercise.getReps());
                    return existingWorkoutExercise;
                } 

                // Se o exercício existe no banco, mas ainda não foi adicionado ao treino, adicionamos
                return new WorkoutExercise(existingWorkout, existingExercise, workoutExercise.getSets(), workoutExercise.getReps());
            })
            .collect(Collectors.toList());

        // Remover exercícios que não estão na nova lista enviada
        List<WorkoutExercise> exercisesToRemove = existingWorkout.getWorkoutExercises().stream()
            .filter(we -> !updatedExerciseIds.contains(we.getExercise().getId()))
            .collect(Collectors.toList());

        // Remover os exercícios do treino
        existingWorkout.getWorkoutExercises().removeAll(exercisesToRemove);
        workoutExerciseRepository.deleteAll(exercisesToRemove);

        existingWorkout.setWorkoutExercises(updatedExercises);

        Workout savedWorkout = workoutRepository.save(existingWorkout);

        // Retorna WorkoutDTO
        return new WorkoutDTO(savedWorkout);
    }

    @Transactional
    public void delete(Long id, Principal principal) {
    	User user = authenticationService.getAuthenticatedUser(principal);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para deletar este treino.");
        }

        workoutExerciseRepository.deleteAll(workout.getWorkoutExercises());

        workoutRepository.delete(workout);
    }

    @Transactional
    public WorkoutExerciseDTO addExerciseToWorkout(Long workoutId, Long exerciseId, int sets, int reps, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Buscar treino e garantir que pertence ao usuário autenticado
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Treino não encontrado"));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para modificar este treino.");
        }

        // Buscar exercício existente no banco
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Exercício não encontrado"));

        // Verificar se o exercício já está no treino
        Optional<WorkoutExercise> existingWorkoutExercise = workout.getWorkoutExercises().stream()
                .filter(we -> we.getExercise().getId().equals(exerciseId))
                .findFirst();

        if (existingWorkoutExercise.isPresent()) {
            // Atualiza os valores de sets e reps se o exercício já estiver no treino
            WorkoutExercise workoutExercise = existingWorkoutExercise.get();
            workoutExercise.setSets(sets);
            workoutExercise.setReps(reps);
            return new WorkoutExerciseDTO(workoutExerciseRepository.save(workoutExercise));
        }

        // Criar e adicionar o novo exercício ao treino
        WorkoutExercise newWorkoutExercise = new WorkoutExercise();
        newWorkoutExercise.setWorkout(workout);
        newWorkoutExercise.setExercise(exercise);
        newWorkoutExercise.setSets(sets);
        newWorkoutExercise.setReps(reps);
        
        // Salvar e retornar um DTO ao invés da entidade bruta
        return new WorkoutExerciseDTO(workoutExerciseRepository.save(newWorkoutExercise));
    }

    

}