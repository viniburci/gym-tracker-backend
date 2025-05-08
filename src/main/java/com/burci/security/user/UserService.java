package com.burci.security.user;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.burci.security.workout.WorkoutDTO;
import com.burci.security.workout.WorkoutExerciseDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;

	public List<User> findAllUsers() {
		return repository.findAll();
	}

	@Transactional
	public List<UserDTO> getAllUsers() {
		List<User> users = repository.findAll();
		return users.stream().map((User user) -> { // Explicit type for user
			// Create UserDTO
			UserDTO userDTO = new UserDTO();
			userDTO.setId(user.getId());
			userDTO.setFirstname(user.getFirstname());
			userDTO.setLastname(user.getLastname());
			userDTO.setEmail(user.getEmail());
			userDTO.setRole(user.getRole());

			// Map workouts, using empty list if workouts is null
			List<WorkoutDTO> workoutDTOs = Optional.ofNullable(user.getWorkouts()).orElse(Collections.emptyList()) // Avoid
																													// NullPointerException
					.stream().map(workout -> { // Explicit type for workout
						WorkoutDTO workoutDTO = new WorkoutDTO();
						workoutDTO.setId(workout.getId());
						workoutDTO.setName(workout.getName());

						// Map workoutExercises, using empty list if workoutExercises is null
						List<WorkoutExerciseDTO> workoutExerciseDTOs = Optional
								.ofNullable(workout.getWorkoutExercises()).orElse(Collections.emptyList()) // Avoid
																											// NullPointerException
								.stream().map(workoutExercise -> { // Explicit type for workoutExercise
									WorkoutExerciseDTO workoutExerciseDTO = new WorkoutExerciseDTO();
									workoutExerciseDTO.setId(workoutExercise.getId());
									workoutExerciseDTO.setExercise(workoutExercise.getExercise());
									workoutExerciseDTO.setSets(workoutExercise.getSets());
									workoutExerciseDTO.setReps(workoutExercise.getReps());
									return workoutExerciseDTO;
								}).collect(Collectors.toList()); // Collect workout exercises

						workoutDTO.setWorkoutExercises(workoutExerciseDTOs);
						return workoutDTO;
					}).collect(Collectors.toList()); // Collect workouts

			userDTO.setWorkouts(workoutDTOs);

			return userDTO;
		}).collect(Collectors.toList()); // Collect all UserDTOs
	}

	public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new IllegalStateException("Wrong password");
		}
		if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
			throw new IllegalStateException("Password are not the same");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		repository.save(user);
	} 

	public UserProjection findProjectionByEmail() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
    	UserProjection projection = repository.findProjectionByEmail(email)
    			.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
    	return projection;
    }
	
	public UserDTO findUserByEmail() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
    	User user = repository.findByEmail(email)
    			.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
    	List<WorkoutDTO> workouts = new ArrayList<>();
    	user.getWorkouts().forEach(w -> workouts.add(new WorkoutDTO(w)));
    	UserDTO dto = new UserDTO(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole(), workouts);
    	return dto;
    }
	
	public UserMinDTO findUserMinDTOByEmail() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		UserMinDTO minDTO = repository.findMinDTOByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado minDTO"));
		return minDTO;
	}
}
