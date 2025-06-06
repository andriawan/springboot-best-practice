package com.andriawan.andresource.controller;

import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.mongo.UserRepository;
import java.util.List;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
  @Autowired private UserRepository userRepository;

  @GetMapping("/user")
  public ResponseEntity<User> getUser() {
    User user = userRepository.save(User.builder().name(Faker.instance().name().name()).build());
    return ResponseEntity.ok(user);
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUser() {
    List<User> users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }
}
