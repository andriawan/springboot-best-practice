package com.andriawan.andresource.service;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthExceptionResponseService {

  public record Response(String message, int status, String exceptionClassName) {}

  @FunctionalInterface
  public interface AuthExceptionMessageResolver {
    Response resolveMessage(AuthenticationException ex);
  }

  private final Map<String, AuthExceptionMessageResolver> resolverMap = new HashMap<>();

  private Response badRequestResponse(AuthenticationException authenticationException) {
    return badRequestResponse(authenticationException, authenticationException.getMessage());
  }

  private Response badRequestResponse(
      AuthenticationException authenticationException, String message) {
    return new Response(
        message, HttpStatus.BAD_REQUEST.value(), authenticationException.getClass().getName());
  }

  private Response unauthorizeResponse(AuthenticationException authenticationException) {
    return new Response(
        authenticationException.getMessage(),
        HttpStatus.UNAUTHORIZED.value(),
        authenticationException.getClass().getName());
  }

  public AuthExceptionResponseService() {
    resolverMap.put("default", exception -> unauthorizeResponse(exception));
    resolverMap.put(
        BadCredentialsException.class.getName(), exception -> badRequestResponse(exception));
    resolverMap.put(
        InsufficientAuthenticationException.class.getName(),
        exception -> badRequestResponse(exception, "Invalid Credentials"));
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
