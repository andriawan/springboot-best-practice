package com.andriawan.andresource.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andriawan.andresource.BaseIntegrationTest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class RateLimiterTest extends BaseIntegrationTest {

  private static final int CAPACITY = 5;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("bucket4j.enabled", () -> "true");
    registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity", () -> CAPACITY);
    registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].time", () -> 1);
    registry.add("bucket4j.filters[0].rate-limits[0].bandwidths[0].unit", () -> "MINUTES");
  }

  @Test
  public void testRateLimiting() {
    int totalRequests = 10;
    List<Integer> responses =
        IntStream.range(0, totalRequests)
            .mapToObj(
                i ->
                    webTestClient
                        .get()
                        .uri("/actuator")
                        .exchange()
                        .expectBody()
                        .returnResult()
                        .getStatus()
                        .value())
            .collect(Collectors.toList());

    long tooManyRequestsCount =
        responses.stream().filter(status -> status == HttpStatus.TOO_MANY_REQUESTS.value()).count();

    assertTrue(tooManyRequestsCount >= totalRequests - CAPACITY);
  }
}
