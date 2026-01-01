package com.hotel.project.backend.services;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Reservation;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    /**
     * Crée une nouvelle réservation
     */
    public static boolean createReservation(int clientId, int roomNumber, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            System.err.println("Start date must be before end date!");
            return false;
        }

        // Vérifier chevauchement
        String overlapCheck = "SELECT COUNT(*) FROM reservations WHERE room_number = ? AND " +
                "(start_date <= ? AND end_date >= ?)";
        List<Integer> results = DatabaseHelper.executeQuery(overlapCheck, rs -> rs.getInt(1), roomNumber, endDate, startDate);
        if (!results.isEmpty() && results.get(0) > 0) {
            System.err.println("Room " + roomNumber + " is already booked for these dates!");
            return false;
        }

        String sql = "INSERT INTO reservations (client_id, room_number, start_date, end_date) VALUES (?, ?, ?, ?)";
        boolean success = DatabaseHelper.executeUpdate(sql, clientId, roomNumber, startDate, endDate);
        if (success) System.out.println("Reservation created successfully!");
        return success;
    }

    /**
     * Récupère toutes les réservations
     */
    public static List<Reservation> getAllReservations() {
        String sql = "SELECT * FROM reservations ORDER BY start_date";
        return DatabaseHelper.executeQuery(sql, rs -> new Reservation(
                rs.getInt("id"),
                rs.getInt("client_id"),
                rs.getInt("room_number"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate()
        ));
    }

    /**
     * Récupère une réservation par ID
     */
    public static Reservation getReservationById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        List<Reservation> results = DatabaseHelper.executeQuery(sql, rs -> new Reservation(
                rs.getInt("id"),
                rs.getInt("client_id"),
                rs.getInt("room_number"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate()
        ), id);

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Met à jour une réservation existante
     */
    public static boolean updateReservation(int id, int clientId, int roomNumber, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            System.err.println("Start date must be before end date!");
            return false;
        }

        // Vérifier chevauchement pour les autres réservations
        String overlapCheck = "SELECT COUNT(*) FROM reservations WHERE room_number = ? AND id != ? AND " +
                "(start_date <= ? AND end_date >= ?)";
        List<Integer> results = DatabaseHelper.executeQuery(overlapCheck, rs -> rs.getInt(1), roomNumber, id, endDate, startDate);
        if (!results.isEmpty() && results.get(0) > 0) {
            System.err.println("Room " + roomNumber + " is already booked for these dates!");
            return false;
        }

        String sql = "UPDATE reservations SET client_id = ?, room_number = ?, start_date = ?, end_date = ? WHERE id = ?";
        boolean success = DatabaseHelper.executeUpdate(sql, clientId, roomNumber, startDate, endDate, id);
        if (success) System.out.println("Reservation updated successfully!");
        return success;
    }

    /**
     * Supprime une réservation par ID
     */
    public static boolean deleteReservation(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        boolean success = DatabaseHelper.executeUpdate(sql, id);
        if (success) System.out.println("Reservation deleted successfully!");
        return success;
    }
}
