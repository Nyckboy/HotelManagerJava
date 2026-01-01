package com.hotel.project.backend.models;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int clientId;
    private int roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructeur complet
    public Reservation(int id, int clientId, int roomNumber, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.clientId = clientId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructeur sans ID (pour création avant insertion en DB)
    public Reservation(int clientId, int roomNumber, LocalDate startDate, LocalDate endDate) {
        this.clientId = clientId;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", roomNumber=" + roomNumber +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
