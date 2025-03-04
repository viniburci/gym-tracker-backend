package com.burci.security.exercise;

import java.io.IOException;
import java.util.List;

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
@RequestMapping("/exercise")
public class ExerciseController {

	@Autowired
	private ExerciseService service;

	@GetMapping
	public ResponseEntity<List<Exercise>> getAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Exercise> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
		byte[] image = service.getImageById(id);
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Exercise> createExercise(@RequestParam("name") String name, @RequestParam("type") Type type,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) throws IOException {

		return ResponseEntity.ok(service.save(name, type, imageFile));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Exercise> updateExercise(
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
