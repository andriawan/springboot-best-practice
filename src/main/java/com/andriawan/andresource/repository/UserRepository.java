package com.andriawan.andresource.repository;

import com.andriawan.andresource.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByIdAndIsDeletedFalse(Long id);

  boolean existsByEmailAndIsDeletedFalse(String email);

  Optional<User> findByEmailAndIsDeletedFalse(String email);

  List<User> findAllByIsDeletedFalse();

  List<User> findAllByIsDeletedFalseAndIsActive(boolean isActive);
}
