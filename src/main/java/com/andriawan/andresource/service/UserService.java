package com.andriawan.andresource.service;

import com.andriawan.andresource.dto.UserCreate;
import com.andriawan.andresource.dto.UserResponse;
import com.andriawan.andresource.dto.UserUpdate;
import com.andriawan.andresource.entity.User;
import com.andriawan.andresource.exception.EntityAlreadyExistsException;
import com.andriawan.andresource.mapper.UserMapper;
import com.andriawan.andresource.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserResponse createUser(UserCreate createDto) {
    log.debug("Creating user with email: {}", createDto.getEmail());

    if (userRepository.existsByEmailAndIsDeletedFalse(createDto.getEmail())) {
      throw new EntityNotFoundException(
          "User with email " + createDto.getEmail() + " already exists");
    }

    User user = userMapper.toEntity(createDto);
    user.setPassword(passwordEncoder.encode(createDto.getPassword()));

    User savedUser = userRepository.save(user);
    log.info("User created successfully with email: {}", savedUser.getEmail());

    return userMapper.toResponseDto(savedUser);
  }

  public UserResponse getUserById(Long id) {
    log.debug("Fetching user with id: {}", id);

    User user =
        userRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

    return userMapper.toResponseDto(user);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserByEmail(String email) {
    log.debug("Fetching user with email: {}", email);

    User user =
        userRepository
            .findByEmailAndIsDeletedFalse(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

    return userMapper.toResponseDto(user);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    log.debug("Fetching all users");

    List<User> users = userRepository.findAllByIsDeletedFalse();
    return userMapper.toResponseDtoList(users);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getActiveUsers() {
    log.debug("Fetching active users");

    List<User> users = userRepository.findAllByIsDeletedFalseAndIsActive(true);
    return userMapper.toResponseDtoList(users);
  }

  public UserResponse updateUser(Long id, UserUpdate updateDto) {
    log.debug("Updating user with id: {}", id);

    User user =
        userRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

    var isEmailDifferent =
        updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail());

    // Check email uniqueness if email is being updated
    if (isEmailDifferent && userRepository.existsByEmailAndIsDeletedFalse(updateDto.getEmail())) {
      throw new EntityAlreadyExistsException(
          "User with email " + updateDto.getEmail() + " already exists");
    }

    userMapper.updateEntityFromDto(updateDto, user);
    User savedUser = userRepository.save(user);

    log.info("User updated successfully with id: {}", id);

    return userMapper.toResponseDto(savedUser);
  }

  public void deleteUser(Long id) {
    log.debug("Deleting user with id: {}", id);

    User user =
        userRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

    user.setIsDeleted(true);
    user.setIsActive(false);
    userRepository.save(user);

    log.info("User deleted successfully with id: {}", id);
  }

  public void activateUser(Long id) {
    log.debug("Activating user with id: {}", id);

    User user =
        userRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

    user.setIsActive(true);
    userRepository.save(user);

    log.info("User activated successfully with id: {}", id);
  }

  public void deactivateUser(Long id) {
    log.debug("Deactivating user with id: {}", id);

    User user =
        userRepository
            .findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

    user.setIsActive(false);
    userRepository.save(user);

    log.info("User deactivated successfully with id: {}", id);
  }
}
