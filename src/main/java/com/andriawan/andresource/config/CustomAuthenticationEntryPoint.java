package com.andriawan.andresource.config;

import com.andriawan.andresource.service.AuthExceptionResponseService;
import com.andriawan.andresource.service.AuthExceptionResponseService.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private AuthExceptionResponseService authExceptionResponseService;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    Response responseForJson = authExceptionResponseService.getMappedResponse(authException);

    Map<String, Object> errorResponse =
        Map.of(
            "timestamp",
            Instant.now().toString(),
            "status",
            responseForJson.status(),
            "message",
            responseForJson.message(),
            "exception",
            responseForJson.exceptionClassName(),
            "path",
            request.getRequestURI());

    response.setContentType("application/json");
    response.setStatus(responseForJson.status());
    objectMapper.writeValue(response.getOutputStream(), errorResponse);
  }
}
