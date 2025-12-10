package com.hotel.project.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // UPDATE THESE WITH YOUR SETTINGS
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root";
    private static final String PASS = ""; // Put your MySQL password here

    public static Connection getConnection() {
        try {
            // 1. Load the Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Connect
            return DriverManager.getConnection(URL, USER, PASS);
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
    
    // Simple test method
    public static void test() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ SUCCESSFULLY CONNECTED TO MYSQL!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
