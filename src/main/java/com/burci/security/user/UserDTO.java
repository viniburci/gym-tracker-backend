package com.burci.security.user;

import java.util.List;

import com.burci.security.workout.WorkoutDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    private List<WorkoutDTO> workouts; // Change this to List<WorkoutDTO>
}