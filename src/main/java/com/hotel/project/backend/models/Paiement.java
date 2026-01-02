package com.hotel.project.backend.models;

import java.time.LocalDate;

public class Paiement {
    public int id;
    public int reservationId;
    public double amount;
    public LocalDate date;

    public Paiement(int id, int reservationId, double amount, LocalDate date) {
        this.id = id;
        this.reservationId = reservationId;
        this.amount = amount;
        this.date = date;
    }
}