package com.andriawan.andresource.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.andriawan.andresource.entity.Role;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

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

  @Autowired private RoleRepository roleRepository;
  @Autowired private TestEntityManager entityManager;

  @Test
  @DisplayName("Should save a role successfully")
  void shouldSaveRole() {
    Role newRole =
        Role.builder().name("ROLE_TEST").createdAt(Instant.now()).updatedAt(Instant.now()).build();

    Role savedRole = roleRepository.save(newRole);

    assertThat(savedRole).isNotNull();
    assertThat(savedRole.getId()).isNotNull();
    assertThat(savedRole.getName()).isEqualTo("ROLE_TEST");
  }

  @Test
  @DisplayName("Should find role by name")
  void shouldFindByName() {
    Role existingRole =
        Role.builder()
            .name("ROLE_EXISTING")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    entityManager.persistAndFlush(existingRole);

    Optional<Role> foundRole = roleRepository.findByName("ROLE_EXISTING");

    assertThat(foundRole).isPresent();
    assertThat(foundRole.get().getName()).isEqualTo("ROLE_EXISTING");
  }

  @Test
  @DisplayName("Should return empty when role not found by name")
  void shouldReturnEmptyWhenRoleNotFoundByName() {
    Optional<Role> foundRole = roleRepository.findByName("ROLE_NONEXISTENT");

    assertThat(foundRole).isEmpty();
  }
}
