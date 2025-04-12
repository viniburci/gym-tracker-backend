package com.burci.security.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  @Query("SELECT u.email as email, u.firstname as firstname, u.lastname as lastname, u.role as role FROM User u WHERE u.email = :email")
  Optional<UserProjection> findProjectionByEmail(String email);
  
  @Query("Select new com.burci.security.user.UserMinDTO(u.firstname, u.lastname, u.email, u.role) from User u where u.email = :email")
  Optional<UserMinDTO> findMinDTOByEmail(String email);
  
}
