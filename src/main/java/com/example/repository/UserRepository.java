package com.example.repository;

import com.example.model.Role;
import com.example.model.User;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserRepository {

  private static final List<User> userList = List.of(
      new User("manager@test.com", "manager", List.of(Role.ROLE_MANAGER)),
      new User("lab@test.com", "lab", List.of(Role.ROLE_LAB)),
      new User("stockman@test.com", "stockman", List.of(Role.ROLE_STOCKMAN)));

  public Optional<User> getUserByEmail(String email) {
    return userList.stream().filter(user -> user.email().equals(email)).findFirst();
  }

}
