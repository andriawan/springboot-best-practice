package com.andriawan.andresource.security;

import com.andriawan.andresource.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class CorsDisabledTest extends BaseIntegrationTest {

  private static final String ORIGIN = "http://localhost:3000";

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("cors.enabled", () -> "false");
  }

  @Test
  void shouldAllowCorsPreflightRequest() {
    webTestClient
        .get()
        .uri("/actuator")
        .header(HttpHeaders.ORIGIN, ORIGIN)
        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
        .exchange()
        .expectStatus()
        .isOk();
  }
}
