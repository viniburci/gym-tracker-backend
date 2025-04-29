package com.burci.security.auth;

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.burci.security.config.JwtService;
import com.burci.security.token.Token;
import com.burci.security.token.TokenRepository;
import com.burci.security.token.TokenType;
import com.burci.security.user.User;
import com.burci.security.user.UserMinDTO;
import com.burci.security.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository repository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;

	@Value("${application.security.jwt.expiration}")
	private Long jwtExpirationMs;

	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder().firstname(request.getFirstname()).lastname(request.getLastname())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole()).build();
		var savedUser = repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var expiresIn = jwtExpirationMs;
		var refreshToken = jwtService.generateRefreshToken(user);

		var expirationDate = getIsoDateForJs(expiresIn);

		saveUserToken(savedUser, jwtToken);
		return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).expiresIn(expiresIn)
				.expirationDate(expirationDate).user(new UserMinDTO(savedUser)).build();
		
		
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var expiresIn = jwtExpirationMs;

		var expirationDate = getIsoDateForJs(expiresIn);

		var refreshToken = jwtService.generateRefreshToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);

		return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).expiresIn(expiresIn)
				.expirationDate(expirationDate).user(new UserMinDTO(user)).build();
	}

	private void saveUserToken(User user, String jwtToken) {
		if (tokenRepository.existsByToken(jwtToken)) {
	        System.out.println("Token já existe no banco.");
	        return;
	    }
		var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false)
				.build();
		tokenRepository.save(token);
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
	    final String refreshToken;
	    final String userEmail;

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        response.getWriter().write("Authorization header is missing or invalid.");
	        return;
	    }

	    refreshToken = authHeader.substring(7);
	    userEmail = jwtService.extractUsername(refreshToken);

	    if (userEmail != null) {
	        var userOptional = this.repository.findByEmail(userEmail);

	        if (userOptional.isEmpty()) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("User not found.");
	            return;
	        }
	        
	        var user = userOptional.get();

	        if (!jwtService.isTokenValid(refreshToken, user)) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Invalid or expired refresh token.");
	            return;
	        }

	        var accessToken = jwtService.generateToken(user);

	        if (!tokenRepository.existsByToken(accessToken)) {
	            revokeAllUserTokens(user);
	            saveUserToken(user, accessToken);
	        }

	        var expiresIn = jwtExpirationMs;
	        var expirationDate = getIsoDateForJs(expiresIn);

	        var authResponse = AuthenticationResponse.builder()
	                .accessToken(accessToken)
	                .refreshToken(refreshToken)
	                .expiresIn(expiresIn)
	                .expirationDate(expirationDate)
	                .user(new UserMinDTO(user))
	                .build();

	        response.setStatus(HttpServletResponse.SC_OK);
	        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
	    }
	}

	public User getAuthenticatedUser(Principal principal) {
		String email = principal.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
	}

	public User getAuthenticatedUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("authService getAuthenticatedUser, getName: " + email);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
	}

	private String getIsoDateForJs(long expiresInMillis) {
		Instant expiration = Instant.now().plusMillis(expiresInMillis);
		ZonedDateTime zonedDate = expiration.atZone(ZoneId.systemDefault());
		return zonedDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}
