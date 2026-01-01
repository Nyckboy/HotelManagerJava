package com.hotel.project.backend.models;

import javafx.beans.property.*;

public class Room {

    private final IntegerProperty number = new SimpleIntegerProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final BooleanProperty available = new SimpleBooleanProperty();

    public Room(int number, String type, double price, boolean available) {
        this.number.set(number);
        this.type.set(type);
        this.price.set(price);
        this.available.set(available);
    }

    public int getNumber() { return number.get(); }
    public IntegerProperty numberProperty() { return number; }

    public String getType() { return type.get(); }
    public StringProperty typeProperty() { return type; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }

    public boolean isAvailable() { return available.get(); }
    public BooleanProperty isAvailableProperty() { return available; }

    public void setAvailable(boolean value) { this.available.set(value); }
}
