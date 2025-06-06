package com.andriawan.andresource.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  @Value("${jwt.token.expired.seconds}")
  long expiredTokenSeconds;

  @Value("${jwt.refresh.expired.seconds}")
  long expiredRefreshSeconds;

  private final JwtEncoder jwtEncoder;

  public record JwtClaimsSetParams(
      long time, Instant now, String scope, Authentication authentication) {}

  public TokenService(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public Map<String, String> generateToken(Authentication authentication) {
    Instant now = Instant.now();
    String scope =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));
    JwtClaimsSet claimsSetAccessToken =
        buildJwtClaimsSet(new JwtClaimsSetParams(expiredTokenSeconds, now, scope, authentication));
    JwtClaimsSet claimsSetRefreshToken =
        buildJwtClaimsSet(
            new JwtClaimsSetParams(expiredRefreshSeconds, now, scope, authentication));
    String accessToken = encodeToken(claimsSetAccessToken);
    String refreshToken = encodeToken(claimsSetRefreshToken);
    return Map.of("access_token", accessToken, "refresh_token", refreshToken);
  }

  private JwtClaimsSet buildJwtClaimsSet(JwtClaimsSetParams params) {
    JwtClaimsSet claimsSet =
        JwtClaimsSet.builder()
            .issuer("apps")
            .issuedAt(params.now())
            .expiresAt(params.now().plus(params.time(), ChronoUnit.SECONDS))
            .subject(params.authentication().getName())
            .claim("scope", params.scope())
            .claim("user_id", "1")
            .build();
    return claimsSet;
  }

  public String encodeToken(JwtClaimsSet jwtClaimsSet) {
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
  }
}
