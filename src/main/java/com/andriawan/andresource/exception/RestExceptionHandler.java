package com.andriawan.andresource.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleUserNotFound(UsernameNotFoundException ex) {
    return Map.of("error", "User not found", "message", ex.getMessage());
  }
}
