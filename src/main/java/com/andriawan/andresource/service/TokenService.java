package com.andriawan.andresource.service;

import com.andriawan.andresource.controller.AuthController.RefreshTokenRequest;
import com.andriawan.andresource.entity.RefreshToken;
import com.andriawan.andresource.entity.Role;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  @Value("${jwt.token.expired.seconds}")
  long expiredTokenSeconds;

  @Value("${jwt.refresh.expired.seconds}")
  long expiredRefreshSeconds;

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;
  private final JpaUserDetailsService jpaUserDetailsService;
  private final RefreshTokenRepository refreshTokenRepository;

  public record JwtClaimsSetParams(
      long time, Instant now, String scope, String username, Long userId) {}

  public Map<String, String> generateToken(Authentication authentication) {
    return generateToken(authentication.getName());
  }

  public Map<String, String> generateToken(User user) {
    return generateToken(user.getEmail());
  }

  public String buildScope(JpaUserDetailsService jpaUserDetailsService) {
    Set<String> roles =
        jpaUserDetailsService.getAuthenticatedUser().getUser().getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
    String scope = roles.stream().collect(Collectors.joining(" "));
    return scope;
  }

  public Map<String, String> generateToken(String username) {
    Instant now = Instant.now();
    Long userId = jpaUserDetailsService.getAuthenticatedUser().getUser().getId();
    String scope = buildScope(jpaUserDetailsService);
    JwtClaimsSet claimsSetAccessToken =
        buildJwtClaimsSet(
            new JwtClaimsSetParams(expiredTokenSeconds, now, scope, username, userId));
    JwtClaimsSet claimsSetRefreshToken =
        buildJwtClaimsSet(
            new JwtClaimsSetParams(expiredRefreshSeconds, now, scope, username, userId));
    String accessToken = encodeToken(claimsSetAccessToken);
    String refreshToken = setupRefreshToken(claimsSetRefreshToken);
    return Map.of("access_token", accessToken, "refresh_token", refreshToken);
  }

  public String setupRefreshToken(JwtClaimsSet claimsSetRefreshToken) {
    String token = encodeToken(claimsSetRefreshToken);
    refreshTokenRepository.save(
        RefreshToken.builder()
            .token(token)
            .user(jpaUserDetailsService.getAuthenticatedUser().getUser())
            .expiresAt(claimsSetRefreshToken.getExpiresAt())
            .build());
    return token;
  }

  public void backlistToken(String token) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("token not found"));
    refreshToken.setBlacklistedAt(Instant.now());
    refreshTokenRepository.save(refreshToken);
  }

  public boolean isBlacklistedRefreshToken(String token) {
    boolean state =
        refreshTokenRepository
            .findByToken(token)
            .map(mapToken -> mapToken.getBlacklistedAt() != null)
            .orElse(true);
    return state;
  }

  private JwtClaimsSet buildJwtClaimsSet(JwtClaimsSetParams params) {
    JwtClaimsSet claimsSet =
        JwtClaimsSet.builder()
            .issuer("apps")
            .issuedAt(params.now())
            .id(UUID.randomUUID().toString())
            .expiresAt(params.now().plus(params.time(), ChronoUnit.SECONDS))
            .subject(params.username())
            .claim("scope", params.scope())
            .claim("user_id", params.userId())
            .build();
    return claimsSet;
  }

  public String encodeToken(JwtClaimsSet jwtClaimsSet) {
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
  }

  public void revokeToken(RefreshTokenRequest request) {
    refreshTokenRepository.deleteByToken(request.token());
  }

  @Transactional(dontRollbackOn = BadCredentialsException.class)
  public Map<String, String> doRefreshToken(RefreshTokenRequest request)
      throws AuthenticationException {
    try {
      Jwt jwt = jwtDecoder.decode(request.token());
      if (isBlacklistedRefreshToken(request.token())) {
        refreshTokenRepository.deleteByToken(request.token());
        throw new BadCredentialsException("refresh token is blacklisted");
      }
      Map<String, String> response = generateToken(jwt.getSubject());
      backlistToken(request.token());
      return response;
    } catch (InsufficientAuthenticationException | BadJwtException e) {
      throw new BadCredentialsException(e.getMessage());
    } catch (Exception e) {
      throw e;
    }
  }
}
