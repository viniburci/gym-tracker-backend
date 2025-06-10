package com.burci.security.config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/csrf")
public class CsrfController {

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request, HttpServletResponse httpResponse) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        
        // Log do token atual
        log.info("Token CSRF solicitado: {}", csrf.getToken());
        
        // Verifica cookie existente
        Cookie[] cookies = request.getCookies();
        String existingToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("XSRF-TOKEN".equals(cookie.getName())) {
                    existingToken = cookie.getValue();
                    log.info("Cookie XSRF-TOKEN existente: {}", existingToken);
                    break;
                }
            }
        }
        
        // Compara tokens
        if (existingToken != null) {
            log.info("Comparação de tokens - Cookie: {}, Atual: {}", existingToken, csrf.getToken());
        } else {
            log.info("Nenhum cookie XSRF-TOKEN encontrado na requisição");
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("token", csrf.getToken());
        response.put("headerName", csrf.getHeaderName());
        response.put("parameterName", csrf.getParameterName());
        response.put("existingCookie", existingToken != null ? existingToken : "no cookie");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> getTest(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        log.info("Token CSRF no endpoint de teste: {}", csrf.getToken());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "GET test successful!");
        response.put("currentToken", csrf.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testCsrf(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        log.info("Token CSRF no endpoint POST de teste: {}", csrf.getToken());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "CSRF test successful!");
        response.put("currentToken", csrf.getToken());
        return ResponseEntity.ok(response);
    }
} 