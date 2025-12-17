package com.hotel.project.frontend.screens;

import com.hotel.project.backend.models.Room;
import com.hotel.project.backend.services.RoomService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomScreen {

    private final RoomService roomService = new RoomService();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public void start(Stage stage) {

        // ---- Title ----
        Label title = new Label("Room Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // ---- Form: Add Room ----
        TextField numberField = new TextField();
        numberField.setPromptText("Room Number");

        TextField typeField = new TextField();
        typeField.setPromptText("Type (Single, Double...)");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addBtn = new Button("Add Room");

        Label statusLabel = new Label("");

        addBtn.setOnAction(e -> {
            executor.submit(() -> {
                try {
                    int num = Integer.parseInt(numberField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    String type = typeField.getText();

                    roomService.createRoom(num, type, price);

                    statusLabel.setText("Room added successfully!");
                } catch (Exception ex) {
                    statusLabel.setText("Error: Invalid data!");
                }
            });
        });

        VBox form = new VBox(10, numberField, typeField, priceField, addBtn, statusLabel);
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        // ---- Table: List rooms ----
        TableView<Room> table = new TableView<>();
        TableColumn<Room, Integer> c1 = new TableColumn<>("Number");
        c1.setCellValueFactory(data -> data.getValue().numberProperty().asObject());

        TableColumn<Room, String> c2 = new TableColumn<>("Type");
        c2.setCellValueFactory(data -> data.getValue().typeProperty());

        TableColumn<Room, Boolean> c3 = new TableColumn<>("Available");
        c3.setCellValueFactory(data -> data.getValue().isAvailableProperty().asObject());

        table.getColumns().addAll(c1, c2, c3);
        table.setPrefHeight(200);

        Button refreshBtn = new Button("Show Available Rooms");

        refreshBtn.setOnAction(e -> {
            executor.submit(() -> {
                var list = roomService.getAvailableRooms();
                table.getItems().setAll(list);
            });
        });

        VBox tableBox = new VBox(10, refreshBtn, table);
        tableBox.setPadding(new Insets(10));
        tableBox.setAlignment(Pos.CENTER);

        // ---- Update Room Status ----
        TextField roomIdField = new TextField();
        roomIdField.setPromptText("Room number");

        CheckBox freeCheck = new CheckBox("Is Available");

        Button updateBtn = new Button("Update Status");

        updateBtn.setOnAction(e -> executor.submit(() -> {
            try {
                int num = Integer.parseInt(roomIdField.getText());
                boolean free = freeCheck.isSelected();
                roomService.updateRoomStatus(num, free);
                statusLabel.setText("Room status updated.");
            } catch (Exception ex) {
                statusLabel.setText("Error while updating!");
            }
        }));

        VBox updateBox = new VBox(10, roomIdField, freeCheck, updateBtn);
        updateBox.setAlignment(Pos.CENTER);

        // ---- Main Layout ----
        VBox root = new VBox(20, title, form, tableBox, updateBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 600, 600));
        stage.setTitle("Manage Rooms");
        stage.show();
    }
}
