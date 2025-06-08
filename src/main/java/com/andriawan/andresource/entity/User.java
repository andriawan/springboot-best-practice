package com.andriawan.andresource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, length = 255)
  private String email;

  @JsonIgnore
  @Column(nullable = false, length = 255)
  private String password;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @PrePersist
  protected void onCreate() {
    ZonedDateTime now = ZonedDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
    this.isActive = true;
    this.isDeleted = false;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = ZonedDateTime.now();
  }
}
