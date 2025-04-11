package com.burci.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;

import com.burci.security.user.User;
import com.burci.security.user.UserDTO;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(service.register(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(service.authenticate(request));
	}

	@PostMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		service.refreshToken(request, response);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getAuthenticatedUser() {
		User user = service.getAuthenticatedUser();
		UserDTO dto = UserDTO.builder().email(user.getEmail()).firstname(user.getFirstname()).lastname(user.getLastname()).role(user.getRole()).build();
		return ResponseEntity.ok(dto);
	}

}
