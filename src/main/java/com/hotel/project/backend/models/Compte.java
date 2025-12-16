package com.hotel.project.backend.models;

import com.hotel.project.backend.Serialisable;

public class Compte implements Serialisable {
  private String username;
  private String password;
  private String role;

  public Compte(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getRole() {
    return role;
  }

  @Override
  public String toString() {
    return username + " (" + role + ")";
  }

  @Override
  public String toCsv() {
    return username + ',' + password + ',' + role;
  }

  public static Compte fromCsv(String line) {
    String[] parts = line.split(",");
    return new Compte(parts[0], parts[1], parts[2]);
  }
}
