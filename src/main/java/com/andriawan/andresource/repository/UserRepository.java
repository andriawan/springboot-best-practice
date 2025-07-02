package com.andriawan.andresource.repository;

import com.andriawan.andresource.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByIdAndIsDeletedFalse(Long id);

  boolean existsByEmailAndIsDeletedFalse(String email);

  Optional<User> findByEmailAndIsDeletedFalse(String email);

  List<User> findAllByIsDeletedFalse();

  List<User> findAllByIsDeletedFalseAndIsActive(boolean isActive);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
  Optional<User> findByEmailWithRoles(@Param("email") String email);
}
