package com.burci.security.user;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
		return ResponseEntity.ok(service.getAllUsers());
    }


    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
    
	@GetMapping("/me")
	public ResponseEntity<UserProjection> getAuthenticatedUser() {
		UserProjection projection = service.findProjectionByEmail();
		return ResponseEntity.ok(projection);
	}
	
	@GetMapping("/me2")
	public ResponseEntity<UserDTO> getAuthenticatedUserDTO() {
		UserDTO dto = service.findUserByEmail();
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping("/mindto")
	public ResponseEntity<UserMinDTO> getAuthenticatedUserMinDTO() {
		UserMinDTO dto = service.findUserMinDTOByEmail();
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping("/auth-info")
	public Map<String, String> getAuthInfo() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    return Map.of(
	        "email", auth.getName(),
	        "authorities", auth.getAuthorities().toString()
	    );
	}

}
