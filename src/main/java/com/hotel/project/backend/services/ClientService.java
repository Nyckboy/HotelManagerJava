package com.hotel.project.backend.services;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Client;
import java.util.List;

public class ClientService {

    // --- CREATE ---
    public static boolean addClient(String firstName, String lastName, String phone) {
        String sql = "INSERT INTO clients (first_name, last_name, phone) VALUES (?, ?, ?)";
        return DatabaseHelper.executeUpdate(sql, firstName, lastName, phone);
    }

    // --- READ ---
    public static List<Client> getAllClients() {
        String sql = "SELECT * FROM clients";
        return DatabaseHelper.executeQuery(sql, rs -> new Client(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("phone")
        ));
    }
    
    // --- DELETE ---
    public static boolean deleteClient(int id) {
        return DatabaseHelper.executeUpdate("DELETE FROM clients WHERE id = ?", id);
    }
}