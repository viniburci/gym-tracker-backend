package com.burci.security.user;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burci.security.auth.AuthenticationService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationService authService;

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

}
