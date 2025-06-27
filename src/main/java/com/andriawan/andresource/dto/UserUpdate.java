package com.andriawan.andresource.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdate {
  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  @Email(message = "Email should be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  private String email;

  private Boolean isActive;
}
