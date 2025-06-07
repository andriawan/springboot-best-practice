package com.andriawan.andresource.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    String message = authException.getMessage();
    int status = HttpServletResponse.SC_UNAUTHORIZED;

    Throwable cause = authException.getCause();
    if (cause instanceof com.nimbusds.jwt.proc.BadJWTException
        || cause instanceof org.springframework.security.oauth2.jwt.JwtException) {
      if (cause.getMessage().toLowerCase().contains("expired")) {
        message = "Token expired";
      } else {
        message = "Invalid token";
      }
    }

    if (authException instanceof BadCredentialsException) {
      status = HttpServletResponse.SC_BAD_REQUEST;
    }

    Map<String, Object> errorResponse =
        Map.of(
            "timestamp",
            Instant.now().toString(),
            "status",
            status,
            "message",
            message,
            "path",
            request.getRequestURI());

    response.setContentType("application/json");
    response.setStatus(status);
    objectMapper.writeValue(response.getOutputStream(), errorResponse);
  }
}
