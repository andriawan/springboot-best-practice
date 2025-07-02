package com.andriawan.andresource.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.andriawan.andresource.entity.Role;
import com.andriawan.andresource.entity.User;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@DataJpaTest
@ActiveProfiles("test")
class UserJpaTest {

  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Autowired private UserRepository userRepository;
  @Autowired private RoleRepository roleRepository;

  private User testUser;
  private Role userRole;
  private Role adminRole;

  @BeforeEach
  void setUp() {
    adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
    userRole = roleRepository.findByName("ROLE_USER").orElse(null);

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    roles.add(adminRole);

    userRepository.deleteAll();

    testUser =
        User.builder()
            .name("Test User")
            .email("test@example.com")
            .password("password")
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .isActive(true)
            .isDeleted(false)
            .roles(roles)
            .build();
    userRepository.save(testUser);
  }

  @Test
  @DisplayName("Should save a user successfully")
  void shouldSaveUser() {
    User newUser =
        User.builder()
            .name("New User")
            .email("new@example.com")
            .password("newpassword")
            .roles(Set.of(userRole))
            .build();

    User savedUser = userRepository.save(newUser);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
  }

  @Test
  @DisplayName("Should find user by email")
  void shouldFindByEmail() {
    Optional<User> foundUser = userRepository.findByEmail("test@example.com");

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("Should find user by ID and isDeletedFalse")
  void shouldFindByIdAndIsDeletedFalse() {
    Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getId()).isEqualTo(testUser.getId());
    assertThat(foundUser.get().getIsDeleted()).isFalse();
  }

  @Test
  @DisplayName("Should return empty when user is deleted")
  void shouldReturnEmptyWhenUserIsDeleted() {
    testUser.setIsDeleted(true);
    userRepository.save(testUser);

    Optional<User> foundUser = userRepository.findByIdAndIsDeletedFalse(testUser.getId());

    assertThat(foundUser).isEmpty();
  }

  @Test
  @DisplayName("Should check if user exists by email and isDeletedFalse")
  void shouldExistsByEmailAndIsDeletedFalse() {
    boolean exists = userRepository.existsByEmailAndIsDeletedFalse("test@example.com");
    assertThat(exists).isTrue();

    boolean notExists = userRepository.existsByEmailAndIsDeletedFalse("nonexistent@example.com");
    assertThat(notExists).isFalse();
  }

  @Test
  @DisplayName("Should find all non-deleted users")
  void shouldFindAllByIsDeletedFalse() {
    User deletedUser =
        User.builder()
            .name("Deleted User")
            .email("deleted@example.com")
            .password("password")
            .isActive(false)
            .isDeleted(true)
            .roles(Set.of(userRole))
            .build();
    userRepository.save(deletedUser);

    List<User> users = userRepository.findAllByIsDeletedFalse();
    assertThat(users).hasSize(1); // Only testUser should be found
    assertThat(users.get(0).getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("Should find all non-deleted and active users")
  void shouldFindAllByIsDeletedFalseAndIsActive() {
    User inactiveUser =
        User.builder()
            .name("Inactive User")
            .email("inactive@example.com")
            .password("password")
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .isActive(false)
            .isDeleted(false)
            .roles(Set.of(userRole))
            .build();
    userRepository.save(inactiveUser);

    List<User> activeUsers = userRepository.findAllByIsDeletedFalseAndIsActive(true);
    assertThat(activeUsers).hasSize(1);
    assertThat(activeUsers.get(0).getEmail()).isEqualTo("test@example.com");

    List<User> inactiveUsers = userRepository.findAllByIsDeletedFalseAndIsActive(false);
    assertThat(inactiveUsers).hasSize(1);
    assertThat(inactiveUsers.get(0).getEmail()).isEqualTo("inactive@example.com");
  }

  @Test
  @DisplayName("Should find user by email with roles eagerly loaded")
  void shouldFindByEmailWithRoles() {
    Optional<User> foundUser = userRepository.findByEmail("test@example.com");

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    assertThat(foundUser.get().getRoles()).isNotEmpty().hasSize(2);
    assertThat(foundUser.get().getRoles())
        .extracting(Role::getName)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
  }
}
