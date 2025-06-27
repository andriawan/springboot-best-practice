package com.andriawan.andresource.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.andriawan.andresource.entity.AuthenticatedUser;
import com.andriawan.andresource.entity.RefreshToken;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

public class TokenServiceTest {

  @Mock private JwtEncoder jwtEncoder;
  @Mock private JwtDecoder jwtDecoder;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private JpaUserDetailsService jpaUserDetailsService;
  @InjectMocks private TokenService tokenService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(tokenService, "expiredTokenSeconds", 3600L);
    ReflectionTestUtils.setField(tokenService, "expiredRefreshSeconds", 7200L);
  }

  @Test
  void generateToken_returnsAccessAndRefreshToken() {
    var user = mock(User.class);
    when(user.getId()).thenReturn(1L);
    when(user.getEmail()).thenReturn("user@example.com");
    var principal = mock(AuthenticatedUser.class);
    when(principal.getUser()).thenReturn(user);
    when(jpaUserDetailsService.getAuthenticatedUser()).thenReturn(principal);

    // Mock jwtEncoder
    var jwtAccess = mock(Jwt.class);
    var jwtRefresh = mock(Jwt.class);

    when(jwtAccess.getTokenValue()).thenReturn("access-token");
    when(jwtRefresh.getTokenValue()).thenReturn("refresh-token");

    when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
        .thenReturn(jwtAccess) // first call = access
        .thenReturn(jwtRefresh); // second call = refresh

    Map<String, String> tokens = tokenService.generateToken(user);

    assertEquals("access-token", tokens.get("access_token"));
    assertEquals("refresh-token", tokens.get("refresh_token"));

    verify(refreshTokenRepository).save(any()); // ensure token is persisted
  }

  @Test
  void isBlacklistedRefreshToken_returnsTrueWhenBlacklisted() {
    var token = new RefreshToken();
    token.setBlacklistedAt(Instant.now());

    when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(token));

    assertTrue(tokenService.isBlacklistedRefreshToken("token"));
  }

  @Test
  void isBlacklistedRefreshToken_returnsFalseWhenNotBlacklisted() {
    var token = new RefreshToken();
    token.setBlacklistedAt(null);

    when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(token));

    assertFalse(tokenService.isBlacklistedRefreshToken("token"));
  }

  @Test
  void backlistnnNonExistingToken_returnException() {
    var token = new RefreshToken();
    token.setBlacklistedAt(null);

    when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> tokenService.backlistToken("token"));
  }

  @Test
  void encodeToken_shouldDelegateToJwtEncoder() {
    var claimsSet =
        JwtClaimsSet.builder()
            .subject("test")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build();

    var jwt = mock(Jwt.class);
    when(jwt.getTokenValue()).thenReturn("encoded-token");
    when(jwtEncoder.encode(any())).thenReturn(jwt);

    var result = tokenService.encodeToken(claimsSet);

    assertEquals("encoded-token", result);
  }
}
