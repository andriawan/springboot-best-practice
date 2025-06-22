package com.andriawan.andresource.security;

import com.andriawan.andresource.config.Security;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.UserRepository;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
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

  @Autowired private WebTestClient webTestClient;

  private ResponseSpec doLogin(String password) throws Exception {
    String loginEndpoint = Security.loginRoute;
    User user = userRepository.findById(1L).orElseThrow();
    var response =
        webTestClient
            .post()
            .uri(loginEndpoint)
            .headers(headers -> headers.setBasicAuth(user.getEmail(), password))
            .exchange();
    return response;
  }

  private Map<String, String> doLoginWithReturn(String password) throws Exception {
    ParameterizedTypeReference<Map<String, String>> typeRef = new ParameterizedTypeReference<>() {};
    var result = doLogin(password).returnResult(typeRef).getResponseBody().blockFirst();
    return result;
  }

  @Test
  void user_can_login_successfully() throws Exception {
    doLogin(DEFAULT_PASSWORD).expectStatus().isOk();
  }

  @Test
  void user_cannot_login_using_wrong_password() throws Exception {
    doLogin("DEFAULT_PASSWORD").expectStatus().isBadRequest();
  }

  @Test
  void user_can_get_own_profile() throws Exception {
    Map<String, String> loginResponse = doLoginWithReturn(DEFAULT_PASSWORD);
    String token = loginResponse.get("access_token");
    webTestClient
        .get()
        .uri("/api/v1/me")
        .headers(headers -> headers.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk();
  }
}
