package com.hotel.project.backend.models;

public class Client {
    public int id;
    public String firstName;
    public String lastName;
    public String phone;

    public Client(int id, String firstName, String lastName, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    
    // Helper for displaying in Dropdowns
    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + id + ")";
    }
}