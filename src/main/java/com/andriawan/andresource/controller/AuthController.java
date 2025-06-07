package com.andriawan.andresource.controller;

import com.andriawan.andresource.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  @Autowired private TokenService tokenService;

  @SecurityRequirement(name = "basicAuth")
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> authenticate(Authentication authentication) {
    return ResponseEntity.ok(tokenService.generateToken(authentication));
  }

  @SecurityRequirement(name = "jwt")
  @PostMapping("/token/refresh")
  public ResponseEntity<Map<String, String>> refreshToken(Authentication authentication) {
    return ResponseEntity.ok(tokenService.generateToken(authentication));
  }
}
