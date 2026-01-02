package com.hotel.project.backend.services;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Paiement;
import com.hotel.project.backend.models.Reservation;

import java.sql.Date; // For SQL dates
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FinanceService {

    // 1. PROCESS PAYMENT (Add money to a reservation)
    public static boolean processPayment(int reservationId, double amount) {
        String sql = "INSERT INTO payments (reservation_id, amount, payment_date) VALUES (?, ?, ?)";
        return DatabaseHelper.executeUpdate(sql, reservationId, amount, LocalDate.now());
    }

    // 2. GET TOTAL REVENUE (Manager Only)
    public static double getTotalRevenue() {
        String sql = "SELECT SUM(amount) FROM payments";
        List<Double> result = DatabaseHelper.executeQuery(sql, rs -> rs.getDouble(1));
        
        if (result.isEmpty() || result.get(0) == null) return 0.0;
        return result.get(0);
    }

    // 3. CALCULATE REMAINING BILL
    // Formula: (Room Price * Nights) - (Total Paid So Far)
    public static double calculateRemainingBill(int reservationId) {
        // Step A: Get Reservation Details (Room Price, Start Date, End Date)
        // We JOIN 'reservations' with 'rooms' to get the price
        String sqlInfo = "SELECT r.start_date, r.end_date, rm.price " +
                         "FROM reservations r " +
                         "JOIN rooms rm ON r.room_number = rm.number " +
                         "WHERE r.id = ?";
        
        List<Double> totalCostList = DatabaseHelper.executeQuery(sqlInfo, rs -> {
            LocalDate start = rs.getDate("start_date").toLocalDate();
            LocalDate end = rs.getDate("end_date").toLocalDate();
            double pricePerNight = rs.getDouble("price");
            
            long nights = ChronoUnit.DAYS.between(start, end);
            if (nights < 1) nights = 1; // Minimum 1 night charge
            
            return nights * pricePerNight;
        }, reservationId);

        if (totalCostList.isEmpty()) return 0.0; // Reservation not found
        double totalCost = totalCostList.get(0);

        // Step B: Get Total Paid
        String sqlPaid = "SELECT SUM(amount) FROM payments WHERE reservation_id = ?";
        List<Double> paidList = DatabaseHelper.executeQuery(sqlPaid, rs -> rs.getDouble(1), reservationId);
        
        double totalPaid = (paidList.isEmpty() || paidList.get(0) == null) ? 0.0 : paidList.get(0);

        return totalCost - totalPaid;
    }

    // 4. GET LATE RESERVATIONS
    // Returns a list of reservations that still owe money (Bill > 0)
    public static List<Reservation> getLateReservations() {
        List<Reservation> lateList = new ArrayList<>();
        List<Reservation> allReservations = ReservationService.getAllReservations(); // Reuse your existing service

        for (Reservation res : allReservations) {
            double remaining = calculateRemainingBill(res.id);
            if (remaining > 0) {
                lateList.add(res);
            }
        }
        return lateList;
    }
}