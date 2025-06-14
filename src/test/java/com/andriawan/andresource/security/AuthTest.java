package com.andriawan.andresource.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andriawan.andresource.config.Security;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthTest {

  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired UserRepository userRepository;

  final String DEFAULT_PASSWORD = "password";

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Autowired MockMvc api;

  private ResultActions doLogin(String password) throws Exception {
    String loginEndpoint = Security.loginRoute;
    User user = userRepository.findById(1L).orElseThrow();
    RequestPostProcessor authBasic =
        SecurityMockMvcRequestPostProcessors.httpBasic(user.getEmail(), password);
    return api.perform(post(loginEndpoint).with(authBasic));
  }

  private Map<String, String> doLoginWithReturn(String password) throws Exception {
    MvcResult result = doLogin(password).andReturn();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
    String token = json.get("access_token").asText();
    String refresh = json.get("refresh_token").asText();
    return Map.of("access_token", token, "refresh_token", refresh);
  }

  @Test
  void user_can_login_successfully() throws Exception {
    doLogin(DEFAULT_PASSWORD).andExpect(status().isOk());
  }

  @Test
  void user_cannot_login_using_wrong_password() throws Exception {
    doLogin("DEFAULT_PASSWORD").andExpect(status().isUnauthorized());
  }

  @Test
  void user_can_get_own_profile() throws Exception {
    Map<String, String> loginResponse = doLoginWithReturn(DEFAULT_PASSWORD);
    String token = loginResponse.get("access_token");
    api.perform(get("/api/v1/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }
}
