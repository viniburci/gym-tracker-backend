package com.burci.security.exercise;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.burci.security.auth.AuthenticationService;
import com.burci.security.user.User;
import com.burci.security.user.UserRepository;
import com.burci.security.user.UserService;
import com.burci.security.workout.Workout;
import com.burci.security.workout.WorkoutExercise;
import com.burci.security.workout.WorkoutRepository;

@Component
public class ExerciseSeeder implements CommandLineRunner {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public ExerciseSeeder(ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
    	Thread.sleep(2000);
        if (exerciseRepository.count() == 0) {
            List<Exercise> exercises = exerciseRepository.saveAll(List.of(
                new Exercise(1L, "Agachamento", Type.PERNA, null, null),
                new Exercise(2L, "Supino", Type.PEITO, null, null),
                new Exercise(3L, "Rosca Direta", Type.BRACO, null, null),
                new Exercise(4L, "Corrida", Type.CARDIO, null, null),
                new Exercise(5L, "Abdominal", Type.CORE, null, null)
            ));
            System.out.println("Exercícios padrão inseridos no banco!");
//            if (workoutRepository.count() == 0) {
//            	User user = userRepository.findByEmail("admin@mail.com").get();
//            	
//                Workout workout = new Workout();
//                workout.setUser(user);
//                workout.setName("Treino Inicial");
//
//                Workout savedWorkout = workoutRepository.save(workout);
//                System.out.println("Workout padrão criado: " + savedWorkout.getName());
//
//                List<WorkoutExercise> workoutExercises = exercises.stream()
//                    .map(exercise -> new WorkoutExercise(null, savedWorkout, exercise, 3, 10))
//                    .toList();
//
//                savedWorkout.setWorkoutExercises(workoutExercises);
//                workoutRepository.save(savedWorkout);
//                System.out.println("Exercícios adicionados ao Workout!");
//            }
        }

    }

}
