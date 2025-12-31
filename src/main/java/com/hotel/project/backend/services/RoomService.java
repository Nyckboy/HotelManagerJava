package com.hotel.project.backend.services;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Room;
import java.util.List;

public class RoomService {

    /**
     * Crée une nouvelle chambre dans la base de données
     * @param number Numéro de la chambre
     * @param type Type de chambre (Single, Double, Suite...)
     * @param price Prix par nuit
     * @return true si la création a réussi, false sinon
     */
    public static boolean createRoom(int number, String type, double price) {
        // Vérifier si la chambre existe déjà
        if (roomExists(number)) {
            System.err.println("Room " + number + " already exists!");
            return false;
        }
        
        // Validation des données
        if (number <= 0) {
            System.err.println("Room number must be positive!");
            return false;
        }
        
        if (type == null || type.trim().isEmpty()) {
            System.err.println("Room type cannot be empty!");
            return false;
        }
        
        if (price < 0) {
            System.err.println("Price cannot be negative!");
            return false;
        }
        
        String sql = "INSERT INTO rooms (number, type, price, is_available) VALUES (?, ?, ?, ?)";
        boolean success = DatabaseHelper.executeUpdate(sql, number, type.trim(), price, true);
        
        if (success) {
            System.out.println("Room " + number + " created successfully!");
        }
        
        return success;
    }

    /**
     * Récupère toutes les chambres de la base de données
     * @return Liste de toutes les chambres
     */
    public static List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms ORDER BY number";
        return DatabaseHelper.executeQuery(sql, rs -> new Room(
            rs.getInt("number"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getBoolean("is_available")
        ));
    }

    /**
     * Récupère une chambre spécifique par son numéro
     * @param number Numéro de la chambre
     * @return La chambre trouvée, ou null si elle n'existe pas
     */
    public static Room getRoom(int number) {
        String sql = "SELECT * FROM rooms WHERE number = ?";
        List<Room> results = DatabaseHelper.executeQuery(sql, rs -> new Room(
            rs.getInt("number"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getBoolean("is_available")
        ), number);
        
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Récupère toutes les chambres disponibles
     * @return Liste des chambres disponibles
     */
    public static List<Room> getAvailableRooms() {
        String sql = "SELECT * FROM rooms WHERE is_available = ? ORDER BY number";
        return DatabaseHelper.executeQuery(sql, rs -> new Room(
            rs.getInt("number"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getBoolean("is_available")
        ), true);
    }

    /**
     * Met à jour le statut de disponibilité d'une chambre
     * @param number Numéro de la chambre
     * @param available true si disponible, false sinon
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean updateRoomStatus(int number, boolean available) {
        if (!roomExists(number)) {
            System.err.println("Room " + number + " does not exist!");
            return false;
        }
        
        String sql = "UPDATE rooms SET is_available = ? WHERE number = ?";
        boolean success = DatabaseHelper.executeUpdate(sql, available, number);
        
        if (success) {
            System.out.println("Room " + number + " status updated to " + (available ? "available" : "occupied"));
        }
        
        return success;
    }

    /**
     * Vérifie si une chambre existe dans la base de données
     * @param number Numéro de la chambre
     * @return true si la chambre existe, false sinon
     */
    public static boolean roomExists(int number) {
        return getRoom(number) != null;
    }

    /**
     * Supprime une chambre de la base de données
     * @param number Numéro de la chambre à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public static boolean deleteRoom(int number) {
        if (!roomExists(number)) {
            System.err.println("Room " + number + " does not exist!");
            return false;
        }
        
        String sql = "DELETE FROM rooms WHERE number = ?";
        boolean success = DatabaseHelper.executeUpdate(sql, number);
        
        if (success) {
            System.out.println("Room " + number + " deleted successfully!");
        }
        
        return success;
    }

    /**
     * Met à jour les informations d'une chambre (type, prix et disponibilité)
     * @param number Numéro de la chambre
     * @param type Nouveau type
     * @param price Nouveau prix
     * @param available Nouveau statut de disponibilité
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean updateRoom(int number, String type, double price, boolean available) {
        if (!roomExists(number)) {
            System.err.println("Room " + number + " does not exist!");
            return false;
        }
        
        if (type == null || type.trim().isEmpty()) {
            System.err.println("Room type cannot be empty!");
            return false;
        }
        
        if (price < 0) {
            System.err.println("Price cannot be negative!");
            return false;
        }
        
        String sql = "UPDATE rooms SET type = ?, price = ?, is_available = ? WHERE number = ?";
        boolean success = DatabaseHelper.executeUpdate(sql, type.trim(), price, available, number);
        
        if (success) {
            System.out.println("Room " + number + " updated successfully!");
        }
        
        return success;
    }
}