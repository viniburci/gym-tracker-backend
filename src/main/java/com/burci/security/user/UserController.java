package com.burci.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
 /*
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
      Map<String, Object> userInfo = new HashMap<>();

      if (authentication != null && authentication.isAuthenticated()) {
        userInfo.put("username", authentication.getName()); // nome do usuário
        userInfo.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Extrai os roles como String
                .collect(Collectors.toList()));
      } else {
        userInfo.put("message", "User is not authenticated");
      }

      return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/user1")
    public String getUser(@AuthenticationPrincipal UserDetails userDetails) {
      return "User Details: " + userDetails.getUsername();
    }

    @GetMapping("/userdetails")
    public String sayHello(@AuthenticationPrincipal UserDetails userDetails, Principal principal) {
      return "UserDetails: " + userDetails + " Principal: " + principal;
    }
*/
}
