package com.andriawan.andresource.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.andriawan.andresource.entity.AuthenticatedUser;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.repository.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private AuthenticatedUser authenticatedUser;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String filteredUsername = URLDecoder.decode(username, StandardCharsets.UTF_8.name());
            Optional<User> user = userRepository.findByEmail(filteredUsername);
            authenticatedUser = user.map(AuthenticatedUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            return authenticatedUser;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
}
