package com.andriawan.andresource.dto;

import java.time.ZonedDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponse {
  private Long id;
  private String name;
  private String email;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  private Boolean isActive;
  private Set<String> roles;
}
