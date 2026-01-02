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

    public int getId() { return id; }
    
    public String getFirstName() { return firstName; }
    
    public String getLastName() { return lastName; }
    
    public String getPhone() { return phone; }
    
    // Helper for displaying in Dropdowns
    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + id + ")";
    }
}