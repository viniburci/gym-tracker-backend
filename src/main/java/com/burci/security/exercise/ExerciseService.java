package com.burci.security.exercise;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRepository repository;

    public List<Exercise> findAll() {
        return repository.findAll();
    }

    public ExerciseDTO findById(Long id) {
         Exercise exercise = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercício com ID " + id + " não encontrado."));
         return toDTO(exercise);
    }

    public ExerciseDTO save(String name, Type type, MultipartFile imageFile) throws IOException {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setType(type);
        if (imageFile != null && !imageFile.isEmpty()) {
            exercise.setImage(imageFile.getBytes());
        }
        
        Exercise savedExercise = repository.save(exercise);
        
        ExerciseDTO dto = new ExerciseDTO(savedExercise);
        return dto;
    }

    public byte[] getImageById(Long id) {
    	Exercise exercise = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercício com ID " + id + " não encontrado."));
        return exercise.getImage();
    }

    public ExerciseDTO update(Long id, String name, Type type, MultipartFile imageFile) throws IOException {
        return repository.findById(id)
                .map(existingExercise -> {
                    existingExercise.setName(name);
                    existingExercise.setType(type);

                    if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                            existingExercise.setImage(imageFile.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao processar a imagem", e);
                        }
                    }

                    return toDTO(repository.save(existingExercise));
                })
                .orElseThrow(() -> new EntityNotFoundException("Exercício com ID " + id + " não encontrado."));
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Exercício com ID " + id + " não encontrado.");
        }
        repository.deleteById(id);
    }

    public void deleteAll() {
        if (repository.count() == 0) {
            throw new IllegalStateException("Não há exercícios para deletar.");
        }
        repository.deleteAll();
    }
    
    public ExerciseDTO toDTO(Exercise exercise) {
        ExerciseDTO dto = new ExerciseDTO();
        dto.setId(exercise.getId());
        dto.setName(exercise.getName());
        dto.setType(exercise.getType().name());
        dto.setImageUrl("/exercises/" + exercise.getId() + "/image");
        return dto;
    }
}