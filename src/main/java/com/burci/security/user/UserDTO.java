package com.burci.security.user;

import java.util.List;

import com.burci.security.workout.WorkoutDTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    private List<WorkoutDTO> workouts; // Change this to List<WorkoutDTO>
}