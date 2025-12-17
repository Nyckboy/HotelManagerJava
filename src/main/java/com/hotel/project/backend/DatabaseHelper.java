package com.hotel.project.backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
  // --- GENERIC READ (SELECT) ---
  // Usage: DatabaseHelper.executeQuery("SELECT * FROM rooms", rs -> new
  // Room(...));
  public static <T> List<T> executeQuery(String sql, RowMapper<T> mapper, Object... params) {
    List<T> list = new ArrayList<>();

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameters(stmt, params);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        list.add(mapper.map(rs));
      }
    } catch (SQLException e) {
      // TODO: handle exception
      e.printStackTrace();
    }
    return list;
  }

  // --- GENERIC WRITE (INSERT, UPDATE, DELETE) ---
  // Usage: DatabaseHelper.executeUpdate("DELETE FROM rooms WHERE id=?", 101);

  public static boolean executeUpdate(String sql, Object... params) {
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameters(stmt, params);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      // TODO: handle exception
      e.printStackTrace();
      return false;
    }
  }

  public static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      stmt.setObject(i + 1, params[i]);
    }
  }
}
