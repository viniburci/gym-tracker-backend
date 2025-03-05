package com.burci.security.workout;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutRepository extends JpaRepository<Workout, Long>{

    @Query("SELECT w FROM Workout w WHERE w.user.email = :email")
    List<Workout> findAllByUser(@Param("email") String email);

    @Query("SELECT w FROM Workout w WHERE w.id = :id AND w.user.email = :email")
    Optional<Workout> findByIdAndUser(@Param("id") Long id, @Param("email") String email);
    
}
