package com.andriawan.andresource.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.andriawan.andresource.config.CustomAuthenticationEntryPoint;
import com.andriawan.andresource.config.InternalFaker;
import com.andriawan.andresource.config.Security;
import com.andriawan.andresource.dto.UserResponse;
import com.andriawan.andresource.repository.UserRepository;
import com.andriawan.andresource.service.AuthExceptionResponseService;
import com.andriawan.andresource.service.JpaUserDetailsService;
import com.andriawan.andresource.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc()
@Import({Security.class, InternalFaker.class})
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired Faker faker;

  @MockitoBean private UserService userService;
  @MockitoBean private UserRepository userRepository;
  @MockitoBean private JpaUserDetailsService jpaUserDetailsService;
  @MockitoBean private AuthExceptionResponseService authExceptionResponseService;
  @MockitoBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  private UserResponse responseDto;

  @BeforeEach
  void setup() {
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
            .roles(Set.of("ROLE_USER"))
            .build();
  }

  @Test
  @DisplayName("GET /users - Success")
  @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
  void testGetAllUsers() throws Exception {
    List<UserResponse> mockUsers = List.of(responseDto);

    when(userService.getAllUsers()).thenReturn(mockUsers);

    mockMvc
        .perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].email").value(responseDto.getEmail()))
        .andExpect(jsonPath("$[0].name").value(responseDto.getName()))
        .andExpect(jsonPath("$[0].roles[0]").value("ROLE_USER"));
  }

  @Test
  @DisplayName("GET /users - No Users Found")
  @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
  void testGetAllUsersNoUsers() throws Exception {
    when(userService.getAllUsers()).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("GET /users - Forbidden (No Role)")
  @WithMockUser(username = "me@example.com", authorities = "SCOPE_ROLE_USER")
  void testGetAllUsersForbiddenNoRole() throws Exception {
    mockMvc.perform(get("/api/v1/users")).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("GET /me - Success")
  @WithMockUser(username = "me@example.com", authorities = "SCOPE_ROLE_USER")
  void testGetAuthenticatedUser() throws Exception {
    Principal principal = () -> "me@example.com";

    when(userService.getUserByEmail("me@example.com")).thenReturn(responseDto);

    mockMvc
        .perform(get("/api/v1/me").principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(responseDto.getEmail()))
        .andExpect(jsonPath("$.name").value(responseDto.getName()))
        .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
  }

  @Test
  @DisplayName("GET /me - Success")
  @WithMockUser(username = "me@example.com", authorities = "SCOPE_ROLE_ADMIN")
  void testGetAuthenticatedUserForAdminRole() throws Exception {
    Principal principal = () -> "me@example.com";

    responseDto.setRoles(Set.of("ROLE_ADMIN"));

    when(userService.getUserByEmail("me@example.com")).thenReturn(responseDto);

    mockMvc
        .perform(get("/api/v1/me").principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(responseDto.getEmail()))
        .andExpect(jsonPath("$.name").value(responseDto.getName()))
        .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
  }

  @Test
  @DisplayName("GET /me - User Not Found")
  @WithMockUser(username = "notfound@example.com", authorities = "SCOPE_ROLE_ADMIN")
  void testGetAuthenticatedUserNotFound() throws Exception {
    Principal principal = () -> "notfound@example.com";

    when(userService.getUserByEmail("notfound@example.com"))
        .thenThrow(new EntityNotFoundException("User not found"));

    mockMvc.perform(get("/api/v1/me").principal(principal)).andExpect(status().isNotFound());
  }
}
