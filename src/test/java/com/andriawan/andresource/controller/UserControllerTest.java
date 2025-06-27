package com.andriawan.andresource.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.andriawan.andresource.dto.UserResponse;
import com.andriawan.andresource.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  private UserResponse responseDto;

  @BeforeEach
  void setup() {
    var faker = Faker.instance();
    var fullName = faker.name().fullName();
    var email = faker.internet().safeEmailAddress();
    responseDto =
        UserResponse.builder()
            .id(1L)
            .name(fullName)
            .email(email)
            .createdAt(Instant.now().atZone(ZoneId.systemDefault()))
            .updatedAt(Instant.now().atZone(ZoneId.systemDefault()))
            .isActive(true)
            .build();
  }

  @Test
  @DisplayName("GET /users - Success")
  void testGetAllUsers() throws Exception {
    List<UserResponse> mockUsers = List.of(responseDto);

    when(userService.getAllUsers()).thenReturn(mockUsers);

    mockMvc
        .perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].email").value(responseDto.getEmail()))
        .andExpect(jsonPath("$[0].name").value(responseDto.getName()));
  }

  @Test
  @DisplayName("GET /me - Success")
  void testGetAuthenticatedUser() throws Exception {
    Principal principal = () -> "me@example.com";

    when(userService.getUserByEmail("me@example.com")).thenReturn(responseDto);

    mockMvc
        .perform(get("/api/v1/me").principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(responseDto.getEmail()))
        .andExpect(jsonPath("$.name").value(responseDto.getName()));
  }

  @Test
  @DisplayName("GET /me - User Not Found")
  void testGetAuthenticatedUserNotFound() throws Exception {
    Principal principal = () -> "notfound@example.com";

    when(userService.getUserByEmail("notfound@example.com"))
        .thenThrow(new EntityNotFoundException("User not found"));

    mockMvc.perform(get("/api/v1/me").principal(principal)).andExpect(status().isNotFound());
  }
}
