package com.andriawan.andresource.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.UserRepository;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JpaUserDetailsServiceTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private JpaUserDetailsService jpaUserDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void loadUserByUsername_shouldReturnAuthenticatedUser_whenUserExists() {
    String encodedEmail = "user%40email.com";
    String decodedEmail = "user@email.com";

    User user = new User();
    user.setEmail(decodedEmail);

    when(userRepository.findByEmail(decodedEmail)).thenReturn(Optional.of(user));

    var result = jpaUserDetailsService.loadUserByUsername(encodedEmail);

    assertNotNull(result);
    assertEquals(decodedEmail, result.getUsername());
    assertEquals(result, jpaUserDetailsService.getAuthenticatedUser());
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserNotFound() {
    String encodedEmail = "nonexistent%40email.com";
    String decodedEmail = "nonexistent@email.com";

    when(userRepository.findByEmail(decodedEmail)).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> jpaUserDetailsService.loadUserByUsername(encodedEmail));
  }

  @Test
  void loadUserByUsername_shouldReturnNull_whenEncodingFails() throws Exception {
    var encoded = "test";

    try (var urlDecoder = mockStatic(URLDecoder.class)) {
      urlDecoder
          .when(() -> URLDecoder.decode("test", "UTF-8"))
          .thenThrow(new UnsupportedEncodingException("boom"));

      urlDecoder
          .when(() -> URLDecoder.decode(eq(encoded), eq(StandardCharsets.UTF_8.name())))
          .thenThrow(new UnsupportedEncodingException());
      var result = jpaUserDetailsService.loadUserByUsername(encoded);
      assertNull(result);
    }
  }
}
