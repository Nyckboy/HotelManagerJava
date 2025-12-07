package com.hotel.project.backend.services;

import java.util.ArrayList;
import java.util.List;

import com.hotel.project.backend.CsvService;
import com.hotel.project.backend.models.Compte;

public class AuthService {
  public static ArrayList<Compte> users = new ArrayList<>();
  private static final String FILE = "users.csv";
  public static Compte currentUser = null;

  public static void init() {
    users.clear();
    List<String> lines = CsvService.loadLines(FILE);
    for (String line : lines) {
      Compte c = Compte.fromCsv(line);
      if (c != null) {
        users.add(c);
      }
    }
    if (users.isEmpty()) {
      System.out.println("No users found ! Creating a default admin ");
      register("admin", "admin", "MANAGER");
    }
  }

  public static Boolean login(String username, String password) {
    for (Compte compte : users) {
      if (compte.username.equals(username) && compte.password.equals(password)) {
        currentUser = compte;
        return true;
      }
    }
    return false;
  }

  public static void logout() {
    currentUser = null;
  }

  public static void register(String username, String password, String role) {
    users.add(new Compte(username, password, role));
    CsvService.saveList(FILE, users);
    System.out.println("User Registered !");
  }

}
