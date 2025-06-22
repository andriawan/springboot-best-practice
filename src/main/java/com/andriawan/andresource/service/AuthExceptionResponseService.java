package com.andriawan.andresource.service;

import com.nimbusds.jwt.proc.BadJWTException;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class AuthExceptionResponseService {

  public record Response(String message, int status, String exceptionClassName) {}

  @FunctionalInterface
  public interface AuthExceptionMessageResolver {
    Response resolveMessage(AuthenticationException ex);
  }

  private final Map<String, AuthExceptionMessageResolver> resolverMap = new HashMap<>();

  public AuthExceptionResponseService() {
    resolverMap.put(
        "default",
        exception ->
            new Response(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.getClass().getName()));
    resolverMap.put(
        BadCredentialsException.class.getName(),
        exception ->
            new Response(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getClass().getName()));
    resolverMap.put(
        InsufficientAuthenticationException.class.getName(),
        exception ->
            new Response(
                "Invalid Credentials",
                HttpStatus.BAD_REQUEST.value(),
                exception.getClass().getName()));
    resolverMap.put(
        AuthenticationException.class.getName(),
        exception -> {
          Throwable cause = exception.getCause();
          String message = "";
          if (cause instanceof BadJWTException || cause instanceof JwtException) {
            message = cause.getMessage().toLowerCase();
            message = message.contains("expired") ? "Token expired" : "Invalid token";
          }
          return new Response(
              message, HttpStatus.UNAUTHORIZED.value(), exception.getClass().getName());
        });
  }

  public Response getMappedResponse(AuthenticationException exception) {
    String className =
        Optional.ofNullable(exception)
            .map(mapException -> mapException.getClass().getName())
            .filter(name -> resolverMap.get(name) != null)
            .orElse("default");
    return resolverMap.get(className).resolveMessage(exception);
  }
}
