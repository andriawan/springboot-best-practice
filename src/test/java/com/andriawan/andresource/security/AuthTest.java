package com.andriawan.andresource.security;

import com.andriawan.andresource.BaseIntegrationTest;
import com.andriawan.andresource.config.Security;
import com.andriawan.andresource.controller.AuthController.RefreshTokenRequest;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

public class AuthTest extends BaseIntegrationTest {

  final String DEFAULT_PASSWORD = "password";
  final String LOGOUT_ROUTE = "/api/v1/auth/logout";
  final String REFRESH_ROUTE = "/api/v1/auth/token/refresh";
  final String DEFAULT_EMAIL = "alice.johnson@example.com";

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("bucket4j.enabled", () -> "false");
  }

  private ResponseSpec doLogin(String password) throws Exception {
    Consumer<HttpHeaders> headerConfigFunc =
        headers -> headers.setBasicAuth(DEFAULT_EMAIL, password);
    return doLoginWithHeader(password, headerConfigFunc);
  }

  private ResponseSpec doLoginWithHeader(String password, Consumer<HttpHeaders> headerConfigFunc)
      throws Exception {
    String loginEndpoint = Security.loginRoute;
    var response = webTestClient.post().uri(loginEndpoint).headers(headerConfigFunc).exchange();
    return response;
  }

  private ResponseSpec doLoginWithId(String password, Long id) throws Exception {
    String loginEndpoint = Security.loginRoute;
    var response =
        webTestClient
            .post()
            .uri(loginEndpoint)
            .headers(headers -> headers.setBasicAuth(DEFAULT_EMAIL, password))
            .exchange();
    return response;
  }

  private ResponseSpec doRefreshToken(String token) throws Exception {
    var response =
        webTestClient
            .post()
            .uri(REFRESH_ROUTE)
            .bodyValue(new RefreshTokenRequest(token))
            .exchange();
    return response;
  }

  private Map<String, String> doLoginWithReturn(String password) throws Exception {
    ParameterizedTypeReference<Map<String, String>> typeRef = new ParameterizedTypeReference<>() {};
    var result = doLogin(password).returnResult(typeRef).getResponseBody().blockFirst();
    return result;
  }

  private Map<String, String> doLoginWithReturn(String password, Long id) throws Exception {
    ParameterizedTypeReference<Map<String, String>> typeRef = new ParameterizedTypeReference<>() {};
    var result = doLoginWithId(password, id).returnResult(typeRef).getResponseBody().blockFirst();
    return result;
  }

  @Test
  void user_get_invalid_auth_basic_header_when_not_provided() throws Exception {
    doLoginWithHeader(DEFAULT_PASSWORD, header -> header.add("test", "sample"))
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void user_get_invalid_or_expired_token() throws Exception {
    var sampleToken =
        "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhcHBzIiwic3ViIjoiYWxpY2Uuam9obnNvbkBleGFtcGxlLmNvbSIsImV4cCI6MTc1MDYwODgxMCwidXNlcl9pZCI6MSwiaWF0IjoxNzUwNjA4NjkwLCJzY29wZSI6IiJ9.BcO7eTXIjx0pQt4kYZdnB2ffy4oOsveDzpo-troM6wDKOqnkMh3E_QfLa7o3BObJP3C2ZLswypryt6bhi7PNXVSV08jJ43NMkQXFf38NYfld91a1VPxRm5-EnVvK9PI239VWftQXfGrGsnMjHWn239J_WiRqLVf5onFuIovxPb8xunSf3lfxM_Jaz0VQbmBtGT6iX9YNvTo8VpiHJLEeKRj6vYf_JI2bzdVH977hXBTxSQhvhkOlnpOa8AQBvQFbA0pbjnGXSfazPEnTTLxojdH2NMb9xXrDpTOx7hE3GLvrKodL2rwwdAeqUh_TjkdFmJooNwT82lifggCKk3_kDQ";
    webTestClient
        .get()
        .uri("/api/v1/me")
        .headers(headers -> headers.setBearerAuth(sampleToken))
        .exchange()
        .expectStatus()
        .isUnauthorized();
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
    Map<String, String> loginResponse = doLoginWithReturn(DEFAULT_PASSWORD, 3L);
    String token = loginResponse.get("access_token");
    webTestClient
        .get()
        .uri("/api/v1/me")
        .headers(headers -> headers.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void user_cannot_get_list_users() throws Exception {
    Map<String, String> loginResponse = doLoginWithReturn(DEFAULT_PASSWORD, 3L);
    String token = loginResponse.get("access_token");
    webTestClient
        .get()
        .uri("/api/v1/users")
        .headers(headers -> headers.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isForbidden();
  }

  @Test
  void user_can_get_logout_sucessfully() throws Exception {
    Map<String, String> loginResponse = doLoginWithReturn(DEFAULT_PASSWORD, 2L);
    String token = loginResponse.get("access_token");
    webTestClient
        .post()
        .uri(LOGOUT_ROUTE)
        .headers(headers -> headers.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void refresh_token_successfully() throws Exception {
    Map<String, String> loginResponse = doLoginWithReturn(DEFAULT_PASSWORD);
    String token = loginResponse.get("refresh_token");
    doRefreshToken(token).expectStatus().isOk();
    doRefreshToken(token).expectStatus().isBadRequest();
    doRefreshToken(token.concat("token")).expectStatus().isBadRequest();
  }
}
