package com.hotel.project.backend.models;

import com.hotel.project.backend.Serialisable;

public class Client implements Serialisable {

    private int id;
    private String fullName;
    private String phone;
    private String email;

    public Client(int id, String fullName, String phone, String email) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public Client(String csvLine) {
        String[] parts = csvLine.split(",");
        this.id = Integer.parseInt(parts[0]);
        this.fullName = parts[1];
        this.phone = parts[2];
        this.email = parts[3];
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toCsv() {
        return id + "," + fullName + "," + phone + "," + email;
    }

    @Override
    public String toString() {
        return "Client #" + id + " - " + fullName;
    }
}

