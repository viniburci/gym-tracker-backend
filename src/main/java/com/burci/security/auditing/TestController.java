package com.burci.security.auditing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping
	public ResponseEntity<Map<String, String>> getTest() {
	    Map<String, String> response = new HashMap<>();
	    response.put("message", "Success!");
	    return ResponseEntity.ok(response);
	}
	
}
