package com.andriawan.andresource.config;

import com.andriawan.andresource.service.JpaUserDetailsService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class Security {

  @Value("${rsa.key.private}")
  private RSAPrivateKey rsaPrivateKey;

  @Value("${rsa.key.public}")
  private RSAPublicKey rsaPublicKey;

  @Value("${cors.origin.allowed}")
  private String[] corsOriginAllowed;

  @Value("${cors.header.allowed}")
  private String[] corsHeaderAllowed;

  @Value("${cors.method.allowed}")
  private String[] corsMethodAllowed;

  @Value("${cors.enabled}")
  private Boolean isCorsEnabled;

  private String[] publicRoute = {
    "/v3/api-docs/*",
    "/v3/api-docs",
    "/swagger-ui/*",
    "/api/v1/auth/token/refresh",
    "/actuator",
    "/actuator/*"
  };

  public static String loginRoute = "/api/v1/auth/login";

  @Autowired private CustomAuthenticationEntryPoint entryPoint;

  @Autowired private JpaUserDetailsService jpaUserDetailsService;

  @Bean
  @Order(1)
  public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
    return http.httpBasic(Customizer.withDefaults())
        .securityMatcher(loginRoute)
        .cors(corsConfig -> corsConfig.configurationSource(buildCorsConfigurationSource()))
        .csrf(crsf -> crsf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint))
        .userDetailsService(jpaUserDetailsService)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            request ->
                request.requestMatchers(publicRoute).permitAll().anyRequest().authenticated())
        .csrf(crsf -> crsf.disable())
        .cors(corsConfig -> corsConfig.configurationSource(buildCorsConfigurationSource()))
        .userDetailsService(jpaUserDetailsService)
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(Customizer.withDefaults()).authenticationEntryPoint(entryPoint))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }

  @Bean
  public JwtDecoder JwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaPublicKey).privateKey(rsaPrivateKey).build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource buildCorsConfigurationSource() {
    if (!isCorsEnabled) return null;
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(corsOriginAllowed));
    config.setAllowedMethods(List.of(corsMethodAllowed));
    config.setAllowedHeaders(List.of(corsHeaderAllowed));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
