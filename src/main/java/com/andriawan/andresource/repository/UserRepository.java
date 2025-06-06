package com.andriawan.andresource.repository;

import com.andriawan.andresource.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
