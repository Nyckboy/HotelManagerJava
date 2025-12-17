package com.hotel.project.backend.services;

import com.hotel.project.backend.models.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomService {

    private static final List<Room> rooms = new ArrayList<>();

    // Create Room
    public synchronized void createRoom(int number, String type, double price) {
        rooms.add(new Room(number, type, price, true));
    }

    // Get room by number
    public Room getRoom(int number) {
        return rooms.stream()
                .filter(r -> r.getNumber() == number)
                .findFirst()
                .orElse(null);
    }

    // Available rooms using streams
    public List<Room> getAvailableRooms() {
        return rooms.stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
    }

    // Update availability
    public synchronized void updateRoomStatus(int number, boolean free) {
        Room r = getRoom(number);
        if (r != null) {
            r.setAvailable(free);
        }
    }
}
