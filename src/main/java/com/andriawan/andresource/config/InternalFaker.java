package com.andriawan.andresource.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFaker {

  @Bean
  public Faker faker() {
    return new Faker();
  }
}
