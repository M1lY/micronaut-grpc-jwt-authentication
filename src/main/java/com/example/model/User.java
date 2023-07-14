package com.example.model;

import java.util.List;

public record User(String email, String password, List<Role> roles) {

  public List<String> getRoleString() {
    return roles.stream().map(Role::toString).toList();
  }
}
