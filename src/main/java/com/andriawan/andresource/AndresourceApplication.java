package com.andriawan.andresource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AndresourceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AndresourceApplication.class, args);
  }
}
