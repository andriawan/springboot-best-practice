package com.andriawan.andresource.repository.mongo;

import com.andriawan.andresource.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {}
