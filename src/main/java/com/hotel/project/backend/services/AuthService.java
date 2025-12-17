package com.hotel.project.backend.services;

// import java.util.ArrayList;
import java.util.List;

// import com.hotel.project.backend.CsvService;
import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Compte;

public class AuthService {
  // public static ArrayList<Compte> users = new ArrayList<>();
  // private static final String FILE = "users.csv";
  // public static Compte currentUser = null;
  //
  // public static void init() {
  // users.clear();
  // List<String> lines = CsvService.loadLines(FILE);
  // for (String line : lines) {
  // Compte c = Compte.fromCsv(line);
  // if (c != null) {
  // users.add(c);
  // }
  // }
  // if (users.isEmpty()) {
  // System.out.println("No users found ! Creating a default admin ");
  // register("admin", "admin", "MANAGER");
  // }
  // }
  //
  // public static Boolean login(String username, String password) {
  // for (Compte compte : users) {
  // if (compte.getUsername().equals(username) &&
  // compte.getPassword().equals(password)) {
  // currentUser = compte;
  // return true;
  // }
  // }
  // return false;
  // }
  //
  // public static void logout() {
  // currentUser = null;
  // }
  //
  // public static void register(String username, String password, String role) {
  // users.add(new Compte(username, password, role));
  // CsvService.saveList(FILE, users);
  // System.out.println("User Registered !");
  // }
  public static Compte currentUser = null;

  public static boolean login(String username, String password) {
    String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

    List<Compte> results = DatabaseHelper.executeQuery(sql, rs -> new Compte(
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("role")), username, password);

    if (!results.isEmpty()) {
      currentUser = results.get(0);
      System.out.println("Login Success: " + currentUser.getUsername());
      return true;
    }

    return false;
  }

  public static void logout() {
    currentUser = null;
  }

  public static boolean register(String username, String password, String role) {
    if (userExists(username)) {
      System.out.println("Error: User " + username + " already exists.");
      return false;
    }

    String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
    return DatabaseHelper.executeUpdate(sql, username, password, role);
  }

  private static boolean userExists(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";
    List<Compte> list = DatabaseHelper.executeQuery(sql, rs -> new Compte(
        rs.getString("username"),
        rs.getString("password"),
        rs.getString("role")), username);

    return !list.isEmpty();
  }

  public static void init() {
    if (!userExists("admin")) {
      System.out.println("Creating default admin...");
      register("admin", "admin", "MANAGER");
    }
  }
}
