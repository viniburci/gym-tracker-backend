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

	    public Exercise findById(Long id) {
	        return repository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Exercício com ID " + id + " não encontrado."));
	    }

	    public Exercise save(String name, Type type, MultipartFile imageFile) throws IOException {
	        Exercise exercise = new Exercise();
	        exercise.setName(name);
	        exercise.setType(type);
	        if (imageFile != null && !imageFile.isEmpty()) {
	            exercise.setImage(imageFile.getBytes());
	        }
	        return repository.save(exercise);
	    }

	    public byte[] getImageById(Long id) {
	        Exercise exercise = findById(id);
	        return exercise.getImage();
	    }

	    public Exercise update(Long id, String name, Type type, MultipartFile imageFile) throws IOException {
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

	                    return repository.save(existingExercise);
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
	
	
}
