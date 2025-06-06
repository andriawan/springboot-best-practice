package com.andriawan.andresource.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("item")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id String id;
  String name;
}
