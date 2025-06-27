package com.andriawan.andresource.mapper;

import com.andriawan.andresource.dto.UserCreate;
import com.andriawan.andresource.dto.UserResponse;
import com.andriawan.andresource.dto.UserUpdate;
import com.andriawan.andresource.entity.User;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "isDeleted", ignore = true)
  User toEntity(UserCreate dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isDeleted", ignore = true)
  @Mapping(target = "password", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(UserUpdate dto, @MappingTarget User entity);

  UserResponse toResponseDto(User entity);

  List<UserResponse> toResponseDtoList(List<User> entities);
}
