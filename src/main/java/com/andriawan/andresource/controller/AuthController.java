package com.andriawan.andresource.controller;

import com.andriawan.andresource.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final TokenService tokenService;

  public record RefreshTokenRequest(String token) {}

  @SecurityRequirement(name = "basicAuth")
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> authenticate(Authentication authentication) {
    return Optional.ofNullable(authentication)
        .map(auth -> ResponseEntity.ok(tokenService.generateToken(auth)))
        .orElseThrow(
            () ->
                new BadCredentialsException(
                    "Please supply auth basic method. see detail at https://datatracker.ietf.org/doc/html/rfc7617"));
  }

  @SecurityRequirement(name = "jwt")
  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(
      Authentication authentication, @RequestHeader("Authorization") String token) {
    var filteredToken = token.replaceAll("Bearer ", "");
    tokenService.revokeToken(new RefreshTokenRequest(filteredToken));
    return ResponseEntity.ok(Map.of("message", "token deleted"));
  }

  @PostMapping("/token/refresh")
  public ResponseEntity<Map<String, String>> refreshToken(@RequestBody RefreshTokenRequest request)
      throws AuthenticationException {
    return ResponseEntity.ok(tokenService.doRefreshToken(request));
  }
}
