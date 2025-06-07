package com.andriawan.andresource.controller;

import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "jwt")
@RequestMapping("/api/v1")
public class UserController {
  @Autowired private UserRepository userRepository;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUser(Principal principal) {
    List<User> users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }
}
