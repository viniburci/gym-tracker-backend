package com.burci.security.exercise;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService service;

    @GetMapping
    public List<ExerciseDTO> getAllExercises() {
        return service.findAll().stream().map(exercise -> {
            ExerciseDTO dto = new ExerciseDTO();
            dto.setId(exercise.getId());
            dto.setName(exercise.getName());
            dto.setType(exercise.getType().name());
            dto.setImageUrl("/exercises/" + exercise.getId() + "/image");
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getExerciseImage(@PathVariable Long id) {
        byte[] image = service.getImageById(id);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String mimeType = "image/jpeg";
        if (image.length > 0) {
            mimeType = "image/jpeg";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mimeType))
            .body(image);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExerciseDTO> createExercise(@RequestParam("name") String name, @RequestParam("type") Type type,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) throws IOException {

        return ResponseEntity.ok(service.save(name, type, imageFile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("type") Type type,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) throws IOException {

        return ResponseEntity.ok(service.update(id, name, type, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllExercises() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
