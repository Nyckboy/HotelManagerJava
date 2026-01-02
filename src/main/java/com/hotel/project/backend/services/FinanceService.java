package com.hotel.project.backend.services;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Paiement;
import com.hotel.project.backend.models.Reservation;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FinanceService {

    // 1. PROCESS PAYMENT
    public static boolean processPayment(int reservationId, double amount) {
        String sql = "INSERT INTO payments (reservation_id, amount, payment_date) VALUES (?, ?, ?)";
        return DatabaseHelper.executeUpdate(sql, reservationId, amount, LocalDate.now());
    }

    // 2. CALCULATE REMAINING BILL
    public static double calculateRemainingBill(int reservationId) {
        // (This code remains the same as before - no changes needed here)
        String sqlInfo = "SELECT r.start_date, r.end_date, rm.price " +
                         "FROM reservations r JOIN rooms rm ON r.room_number = rm.number " +
                         "WHERE r.id = ?";
        List<Double> totalCostList = DatabaseHelper.executeQuery(sqlInfo, rs -> {
            LocalDate start = rs.getDate("start_date").toLocalDate();
            LocalDate end = rs.getDate("end_date").toLocalDate();
            double price = rs.getDouble("price");
            long nights = ChronoUnit.DAYS.between(start, end);
            if (nights < 1) nights = 1; 
            return nights * price;
        }, reservationId);

        if (totalCostList.isEmpty()) return 0.0;
        double totalCost = totalCostList.get(0);

        String sqlPaid = "SELECT SUM(amount) FROM payments WHERE reservation_id = ?";
        List<Double> paidList = DatabaseHelper.executeQuery(sqlPaid, rs -> rs.getDouble(1), reservationId);
        double totalPaid = (paidList.isEmpty() || paidList.get(0) == null) ? 0.0 : paidList.get(0);

        return totalCost - totalPaid;
    }

    // --- NEW: GET LATE RESERVATIONS (WITH CLIENT NAMES!) ---
    public static List<Reservation> getLateReservations() {
        List<Reservation> lateList = new ArrayList<>();

        // SQL JOIN to get the Client Name
        String sql = "SELECT r.*, c.first_name, c.last_name " +
                     "FROM reservations r " +
                     "JOIN clients c ON r.client_id = c.id";

        List<Reservation> allRes = DatabaseHelper.executeQuery(sql, rs -> {
            Reservation res = new Reservation(
                rs.getInt("id"),
                rs.getInt("client_id"),
                rs.getInt("room_number"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate()
            );
            // MANUALLY SET THE NAME
            res.clientName = rs.getString("first_name") + " " + rs.getString("last_name");
            return res;
        });

        // Filter only those who owe money
        for (Reservation res : allRes) {
            if (calculateRemainingBill(res.getId()) > 0) {
                lateList.add(res);
            }
        }
        return lateList;
    }

    // --- NEW: GET PAYMENT HISTORY (FOR THE PAID LIST) ---
    public static List<Paiement> getAllPayments() {
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        return DatabaseHelper.executeQuery(sql, rs -> new Paiement(
            rs.getInt("id"),
            rs.getInt("reservation_id"),
            rs.getDouble("amount"),
            rs.getDate("payment_date").toLocalDate()
        ));
    }
}