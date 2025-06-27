package com.andriawan.andresource.security;

import com.andriawan.andresource.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class CorsTest extends BaseIntegrationTest {

  private static final String ORIGIN = "http://localhost:3000";

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("cors.enabled", () -> "true");
    registry.add("cors.origin.allowed", () -> ORIGIN);
    registry.add("cors.method.allowed", () -> "GET");
    registry.add("git.closest.tag.name", () -> "");
    registry.add("git.build.version", () -> "");
  }

  @Test
  void shouldAllowCorsPreflightRequest() {
    webTestClient
        .options()
        .uri("/actuator")
        .header(HttpHeaders.ORIGIN, ORIGIN)
        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ORIGIN)
        .expectHeader()
        .valuesMatch(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET");
  }
}
