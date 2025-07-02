package com.andriawan.andresource.service;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andriawan.andresource.dto.UserCreate;
import com.andriawan.andresource.dto.UserResponse;
import com.andriawan.andresource.dto.UserUpdate;
import com.andriawan.andresource.entity.Role;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.exception.EntityAlreadyExistsException;
import com.andriawan.andresource.mapper.UserMapper;
import com.andriawan.andresource.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.datafaker.Faker;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  private User testUser;
  private UserCreate createDto;
  private UserUpdate updateDto;
  private UserResponse responseDto;
  private Role userRole;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    var faker = Faker.instance();
    var fullName = faker.name().fullName();
    var email = faker.internet().safeEmailAddress();
    var password = faker.internet().password();

    userRole = Role.builder().id(1).name("ROLE_USER").build();

    testUser =
        User.builder()
            .id(1L)
            .name(fullName)
            .email(email)
            .password(password)
            .createdAt(ZonedDateTime.now())
            .updatedAt(ZonedDateTime.now())
            .isActive(true)
            .isDeleted(false)
            .roles(Set.of(userRole))
            .build();

    createDto =
        UserCreate.builder()
            .name(fullName)
            .email(email)
            .password(faker.internet().password())
            .build();

    updateDto = UserUpdate.builder().name(fullName).email(email).isActive(true).build();

    responseDto =
        UserResponse.builder()
            .id(1L)
            .name(fullName)
            .email(email)
            .createdAt(testUser.getCreatedAt())
            .updatedAt(testUser.getUpdatedAt())
            .isActive(true)
            .roles(Set.of("ROLE_USER"))
            .build();
  }

  @Test
  @DisplayName("Should create user successfully")
  void createUser_Success() {
    // Given
    when(userRepository.existsByEmailAndIsDeletedFalse(createDto.getEmail())).thenReturn(false);
    when(userMapper.toEntity(createDto)).thenReturn(testUser);
    when(passwordEncoder.encode(createDto.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(userMapper.toResponseDto(testUser)).thenReturn(responseDto);

    // When
    UserResponse result = userService.createUser(createDto);

    // Then
    assertNotNull(result);
    assertEquals(result.getName(), testUser.getName());
    assertEquals(result.getEmail(), testUser.getEmail());
    assertEquals(result.getId(), 1L);

    verify(userRepository).existsByEmailAndIsDeletedFalse(createDto.getEmail());
    verify(userMapper).toEntity(createDto);
    verify(passwordEncoder).encode(createDto.getPassword());
    verify(userRepository).save(any(User.class));
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  @DisplayName("Should throw exception when creating user with existing email")
  void createUser_EmailAlreadyExists_ThrowsException() {
    // Given
    when(userRepository.existsByEmailAndIsDeletedFalse(createDto.getEmail())).thenReturn(true);

    ThrowingRunnable runnable = () -> userService.createUser(createDto);

    // When & Then
    assertThrows(EntityNotFoundException.class, runnable);

    verify(userRepository).existsByEmailAndIsDeletedFalse(createDto.getEmail());
    verify(userMapper, never()).toEntity(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should get user by ID successfully")
  void getUserById_Success() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    when(userMapper.toResponseDto(testUser)).thenReturn(responseDto);

    // When
    UserResponse result = userService.getUserById(1L);

    // Then
    assertNotNull(result);
    assertEquals(result.getId(), 1L);

    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  void findByNonExistingId_returnException() {

    when(userRepository.findByIdAndIsDeletedFalse(eq(123L))).thenReturn(Optional.ofNullable(null));

    ThrowingRunnable runnable = () -> userService.getUserById(123L);

    assertThrows(EntityNotFoundException.class, runnable);
  }

  @Test
  @DisplayName("Should get user by email successfully")
  void getUserByEmail_Success() {
    // Given
    when(userRepository.findByEmailAndIsDeletedFalse(testUser.getEmail()))
        .thenReturn(Optional.of(testUser));
    when(userMapper.toResponseDto(testUser)).thenReturn(responseDto);

    // When
    UserResponse result = userService.getUserByEmail(testUser.getEmail());

    // Then
    assertNotNull(result);
    assertEquals(result.getEmail(), testUser.getEmail());
    verify(userRepository).findByEmailAndIsDeletedFalse(testUser.getEmail());
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  @DisplayName("Should throw exception when user not found by email")
  void getUserByEmail_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByEmailAndIsDeletedFalse(testUser.getEmail()))
        .thenReturn(Optional.empty());

    ThrowingRunnable runnable = () -> userService.getUserByEmail(testUser.getEmail());

    // When & Then
    assertThrows(EntityNotFoundException.class, runnable);
  }

  @Test
  @DisplayName("Should get all users successfully")
  void getAllUsers_Success() {
    // Given
    List<User> users = Arrays.asList(testUser);
    List<UserResponse> responseDtos = Arrays.asList(responseDto);

    when(userRepository.findAllByIsDeletedFalse()).thenReturn(users);
    when(userMapper.toResponseDtoList(users)).thenReturn(responseDtos);

    // When
    List<UserResponse> result = userService.getAllUsers();

    // Then
    assertFalse(result.isEmpty());
    assertEquals(result.getFirst().getId(), 1L);
    verify(userRepository).findAllByIsDeletedFalse();
    verify(userMapper).toResponseDtoList(users);
  }

  @Test
  @DisplayName("Should get active users successfully")
  void getActiveUsers_Success() {
    // Given
    List<User> users = Arrays.asList(testUser);
    List<UserResponse> responseDtos = Arrays.asList(responseDto);

    when(userRepository.findAllByIsDeletedFalseAndIsActive(true)).thenReturn(users);
    when(userMapper.toResponseDtoList(users)).thenReturn(responseDtos);

    // When
    List<UserResponse> result = userService.getActiveUsers();

    // Then
    assertFalse(result.isEmpty());
    assertEquals(result.getFirst().getId(), 1L);
    verify(userRepository).findAllByIsDeletedFalseAndIsActive(true);
    verify(userMapper).toResponseDtoList(users);
  }

  @Test
  @DisplayName("Should update user successfully")
  void updateUser_Success() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.existsByEmailAndIsDeletedFalse(updateDto.getEmail())).thenReturn(false);
    when(userRepository.save(testUser)).thenReturn(testUser);
    when(userMapper.toResponseDto(testUser)).thenReturn(responseDto);

    updateDto.setEmail("modified@email.com");

    // When
    UserResponse result = userService.updateUser(1L, updateDto);

    // Then
    assertNotNull(result);
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository).existsByEmailAndIsDeletedFalse(updateDto.getEmail());
    verify(userMapper).updateEntityFromDto(updateDto, testUser);
    verify(userRepository).save(testUser);
    verify(userMapper).toResponseDto(testUser);
  }

  @Test
  @DisplayName("Should update user without changing email")
  void updateUser_SameEmail_Success() {
    // Given
    updateDto.setEmail(testUser.getEmail()); // Same email
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(testUser)).thenReturn(testUser);
    when(userMapper.toResponseDto(testUser)).thenReturn(responseDto);

    // When
    UserResponse result = userService.updateUser(1L, updateDto);

    // Then
    assertNotNull(result);
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository, never()).existsByEmailAndIsDeletedFalse(any());
    verify(userMapper).updateEntityFromDto(updateDto, testUser);
    verify(userRepository).save(testUser);
  }

  @Test
  @DisplayName("Should throw exception when updating user with null email")
  void updateUser_EmailNull_ThrowsException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    updateDto.setEmail(null);
    when(userRepository.existsByEmailAndIsDeletedFalse(updateDto.getEmail())).thenReturn(true);
    verify(userRepository, never()).existsByEmailAndIsDeletedFalse(any());
  }

  @Test
  @DisplayName("Should throw exception when updating user with existing email")
  void updateUser_EmailAlreadyExists_ThrowsException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    updateDto.setEmail("modified@mail.com");
    when(userRepository.existsByEmailAndIsDeletedFalse(updateDto.getEmail())).thenReturn(true);

    ThrowingRunnable runnable = () -> userService.updateUser(1L, updateDto);

    // When & Then
    assertThrows(EntityAlreadyExistsException.class, runnable);
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent user")
  void updateUser_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

    ThrowingRunnable runnable = () -> userService.updateUser(1L, updateDto);

    // When & Then
    assertThrows(EntityNotFoundException.class, runnable);
  }

  @Test
  @DisplayName("Should delete user successfully")
  void deleteUser_Success() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(testUser)).thenReturn(testUser);

    // When
    userService.deleteUser(1L);

    // Then
    assertTrue(testUser.getIsDeleted());
    assertFalse(testUser.getIsActive());
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository).save(testUser);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent user")
  void deleteUser_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

    ThrowingRunnable runnable = () -> userService.deleteUser(1L);

    // When & Then
    assertThrows(EntityNotFoundException.class, runnable);
  }

  @Test
  @DisplayName("Should activate user successfully")
  void activateUser_Success() {
    // Given
    testUser.setIsActive(false);
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(testUser)).thenReturn(testUser);

    // When
    userService.activateUser(1L);

    // Then
    assertTrue(testUser.getIsActive());
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository).save(testUser);
  }

  @Test
  @DisplayName("Should deactivate user successfully")
  void deactivateUser_Success() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(testUser)).thenReturn(testUser);

    // When
    userService.deactivateUser(1L);

    // Then
    assertFalse(testUser.getIsActive());
    verify(userRepository).findByIdAndIsDeletedFalse(1L);
    verify(userRepository).save(testUser);
  }

  @Test
  @DisplayName("Should throw exception when activating non-existent user")
  void activateUser_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

    ThrowingRunnable runnable = () -> userService.activateUser(1L);

    // When & Then
    assertThrows(EntityNotFoundException.class, runnable);
  }

  @Test
  @DisplayName("Should throw exception when deactivating non-existent user")
  void deactivateUser_UserNotFound_ThrowsException() {
    // Given
    when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

    ThrowingRunnable runnable = () -> userService.deactivateUser(1L);

    // When & Then
    assertThrows(EntityNotFoundException.class, runnable);
  }
}
