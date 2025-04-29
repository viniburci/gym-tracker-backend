package com.burci.security.exercise;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSeeder implements CommandLineRunner {

    private final ExerciseRepository exerciseRepository;

    public ExerciseSeeder(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (exerciseRepository.count() == 0) {
            exerciseRepository.saveAll(List.of(
                new Exercise(1L, "Agachamento", Type.PERNA, null, null),
                new Exercise(2L, "Supino", Type.PEITO, null, null),
                new Exercise(3L, "Rosca Direta", Type.BRACO, null, null),
                new Exercise(4L, "Corrida", Type.CARDIO, null, null),
                new Exercise(5L, "Abdominal", Type.CORE, null, null)
            ));
            System.out.println("Exercícios padrão inseridos no banco!");
        }
    }

}
