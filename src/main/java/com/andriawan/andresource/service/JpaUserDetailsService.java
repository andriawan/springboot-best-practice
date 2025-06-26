package com.andriawan.andresource.service;

import com.andriawan.andresource.entity.AuthenticatedUser;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.UserRepository;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private AuthenticatedUser authenticatedUser;

  public AuthenticatedUser getAuthenticatedUser() {
    return authenticatedUser;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      String filteredUsername = URLDecoder.decode(username, StandardCharsets.UTF_8.name());
      Optional<User> user = userRepository.findByEmail(filteredUsername);
      authenticatedUser =
          user.map(AuthenticatedUser::new)
              .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
      return authenticatedUser;
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }
}
