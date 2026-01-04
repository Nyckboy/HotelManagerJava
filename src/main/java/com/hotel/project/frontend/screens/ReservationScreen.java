package com.hotel.project.frontend.screens;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Reservation;
import com.hotel.project.backend.services.ReservationService;
import com.hotel.project.frontend.ScreenManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationScreen {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static TableView<Reservation> table;
    private static ObservableList<Reservation> reservations;

    public static Scene getScene() {
        // --- 1. MAIN LAYOUT ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");

        // --- 2. HEADER ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;");
        
        Label title = new Label("📅 RESERVATION MANAGER");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);

        // --- 3. CENTER: TABLE & TOOLBAR ---
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));

        // TOOLBAR
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_RIGHT);

        Button addBtn = createButton("➕ New Booking", "#27ae60");
        Button editBtn = createButton("✎ Edit", "#2980b9");
        Button deleteBtn = createButton("🗑 Delete", "#c0392b");

        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn);

        // TABLE SETUP
        setupTable();
        
        // Wrap Table in a Card
        VBox tableCard = new VBox(10, toolbar, table);
        tableCard.setPadding(new Insets(15));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        VBox.setVgrow(table, Priority.ALWAYS);

        centerBox.getChildren().add(tableCard);
        root.setCenter(centerBox);

        // --- 4. BOTTOM: NAVIGATION ---
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(15, 20, 15, 20));
        bottomBar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        
        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        backBtn.setPrefHeight(35);
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));
        
        bottomBar.getChildren().add(backBtn);
        root.setBottom(bottomBar);

        // --- 5. ACTIONS ---

        addBtn.setOnAction(e -> {
            ReservationFormData data = showReservationForm(null);
            if (data != null) {
                if (ReservationService.createReservation(data.clientId, data.roomNumber, data.startDate, data.endDate)) {
                    refreshTable();
                }
            }
        });

        editBtn.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Please select a reservation."); return; }

            ReservationFormData data = showReservationForm(selected);
            if (data != null) {
                if (ReservationService.updateReservation(selected.getId(), data.clientId, data.roomNumber, data.startDate, data.endDate)) {
                    refreshTable();
                }
            }
        });

        deleteBtn.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Please select a reservation."); return; }

            if (confirm("Delete reservation #" + selected.getId() + "?")) {
                if (ReservationService.deleteReservation(selected.getId())) {
                    refreshTable();
                }
            }
        });

        return new Scene(root, 850, 550);
    }

    // --- HELPER: SETUP TABLE ---
    private static void setupTable() {
        table = new TableView<>();
        reservations = FXCollections.observableArrayList(ReservationService.getAllReservations());
        table.setItems(reservations);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Reservation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        // If you added 'clientName' to Reservation.java, change "clientId" to "clientName" here
        TableColumn<Reservation, Integer> clientCol = new TableColumn<>("Client ID"); 
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));

        TableColumn<Reservation, Integer> roomCol = new TableColumn<>("Room #");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Reservation, String> startCol = new TableColumn<>("Check-In");
        startCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getStartDate().format(DATE_FORMAT)));

        TableColumn<Reservation, String> endCol = new TableColumn<>("Check-Out");
        endCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getEndDate().format(DATE_FORMAT)));

        table.getColumns().addAll(idCol, clientCol, roomCol, startCol, endCol);
    }

    private static void refreshTable() {
        reservations.setAll(ReservationService.getAllReservations());
    }

    // --- HELPER: SHOW FORM DIALOG ---
    private static ReservationFormData showReservationForm(Reservation existing) {
        Dialog<ReservationFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "New Reservation" : "Edit Reservation");
        dialog.setHeaderText(existing == null ? "Create a new booking" : "Modify booking #" + existing.getId());

        // LOAD DATA
        Map<String, Integer> clientMap = new HashMap<>();
        List<String> clientNames = DatabaseHelper.executeQuery("SELECT id, first_name, last_name FROM clients", rs -> {
            String name = rs.getString("first_name") + " " + rs.getString("last_name");
            clientMap.put(name, rs.getInt("id"));
            return name;
        });

        List<Integer> rooms = DatabaseHelper.executeQuery("SELECT number FROM rooms WHERE is_available = 1", rs -> rs.getInt("number"));
        // If editing, add the current room even if it's "occupied" by this reservation
        if (existing != null && !rooms.contains(existing.getRoomNumber())) {
            rooms.add(existing.getRoomNumber());
        }

        // FORM FIELDS
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> clientCombo = new ComboBox<>(FXCollections.observableArrayList(clientNames));
        clientCombo.setPromptText("Select Client");
        clientCombo.setPrefWidth(200);

        ComboBox<Integer> roomCombo = new ComboBox<>(FXCollections.observableArrayList(rooms));
        roomCombo.setPromptText("Select Room");
        roomCombo.setPrefWidth(200);

        DatePicker startPicker = new DatePicker(existing != null ? existing.getStartDate() : LocalDate.now());
        DatePicker endPicker = new DatePicker(existing != null ? existing.getEndDate() : LocalDate.now().plusDays(1));

        grid.add(new Label("Client:"), 0, 0); grid.add(clientCombo, 1, 0);
        grid.add(new Label("Room:"), 0, 1);   grid.add(roomCombo, 1, 1);
        grid.add(new Label("Start:"), 0, 2);  grid.add(startPicker, 1, 2);
        grid.add(new Label("End:"), 0, 3);    grid.add(endPicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // PRE-SELECT VALUES
        if (existing != null) {
            clientMap.forEach((name, id) -> {
                if (id == existing.getClientId()) clientCombo.getSelectionModel().select(name);
            });
            roomCombo.getSelectionModel().select(Integer.valueOf(existing.getRoomNumber()));
        }

        // CONVERT RESULT
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (clientCombo.getValue() == null || roomCombo.getValue() == null || startPicker.getValue() == null) {
                    showAlert("All fields are required!");
                    return null;
                }
                return new ReservationFormData(clientMap.get(clientCombo.getValue()), roomCombo.getValue(), startPicker.getValue(), endPicker.getValue());
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        return btn;
    }

    private static class ReservationFormData {
        int clientId; int roomNumber; LocalDate startDate; LocalDate endDate;
        ReservationFormData(int c, int r, LocalDate s, LocalDate e) {
            clientId = c; roomNumber = r; startDate = s; endDate = e;
        }
    }

    private static void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.showAndWait();
    }

    private static boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
