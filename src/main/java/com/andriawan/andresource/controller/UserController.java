package com.andriawan.andresource.controller;

import com.andriawan.andresource.dto.UserResponse;
import com.andriawan.andresource.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "jwt")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<UserResponse>> getAllUser(Principal principal) {
    List<UserResponse> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getAuthenticatedUser(Principal principal) {
    try {
      UserResponse user = userService.getUserByEmail(principal.getName());
      return ResponseEntity.ok(user);

    } catch (EntityNotFoundException e) {
      throw new UsernameNotFoundException("User not found");
    }
  }
}
