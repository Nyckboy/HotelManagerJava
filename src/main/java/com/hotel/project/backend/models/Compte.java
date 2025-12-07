package com.hotel.project.backend.models;

import com.hotel.project.backend.Serialisable;

public class Compte implements Serialisable {
  public String username;
  public String password;
  public String role;

  public Compte(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
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
