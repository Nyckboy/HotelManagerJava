package com.hotel.project.frontend.screens;

import com.hotel.project.backend.DatabaseHelper;
import com.hotel.project.backend.models.Reservation;
import com.hotel.project.backend.services.ReservationService;
import com.hotel.project.frontend.ScreenManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationScreen {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;

    public static Scene getScene() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // ================= TABLE =================
        TableView<Reservation> table = new TableView<>();
        ObservableList<Reservation> reservations =
                FXCollections.observableArrayList(ReservationService.getAllReservations());
        table.setItems(reservations);

        TableColumn<Reservation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());

        TableColumn<Reservation, Integer> clientCol = new TableColumn<>("Client ID");
        clientCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getClientId()).asObject());

        TableColumn<Reservation, Integer> roomCol = new TableColumn<>("Chambre");
        roomCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getRoomNumber()).asObject());

        TableColumn<Reservation, String> startCol = new TableColumn<>("Début");
        startCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getStartDate().format(DATE_FORMAT)));

        TableColumn<Reservation, String> endCol = new TableColumn<>("Fin");
        endCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEndDate().format(DATE_FORMAT)));

        table.getColumns().addAll(idCol, clientCol, roomCol, startCol, endCol);

        // ================= BOUTONS =================
        Button addBtn = new Button("Ajouter");
        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");

        Button backBtn = new Button("← Back");
            backBtn.setOnAction(e -> {
            ScreenManager.setScene(DashboardScreen.getScene());
        });

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn, backBtn);

        layout.getChildren().addAll(table, buttons);

        // ================= ACTIONS =================

        addBtn.setOnAction(e -> {
            ReservationFormData data = showReservationForm(null);
            if (data != null) {
                if (ReservationService.createReservation(
                        data.clientId, data.roomNumber, data.startDate, data.endDate)) {
                    reservations.setAll(ReservationService.getAllReservations());
                }
            }
        });

        editBtn.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Sélectionnez une réservation.");
                return;
            }

            ReservationFormData data = showReservationForm(selected);
            if (data != null) {
                if (ReservationService.updateReservation(
                        selected.getId(), data.clientId, data.roomNumber,
                        data.startDate, data.endDate)) {
                    reservations.setAll(ReservationService.getAllReservations());
                }
            }
        });

        deleteBtn.setOnAction(e -> {
            Reservation selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Sélectionnez une réservation.");
                return;
            }

            if (confirm("Supprimer la réservation ID " + selected.getId() + " ?")) {
                if (ReservationService.deleteReservation(selected.getId())) {
                    reservations.setAll(ReservationService.getAllReservations());
                }
            }
        });

        return new Scene(layout, 750, 450);
    }

    // ================= FORMULAIRE =================
    private static ReservationFormData showReservationForm(Reservation existing) {

        Dialog<ReservationFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Ajouter Réservation" : "Modifier Réservation");

        // -------- CLIENTS --------
        Map<String, Integer> clientMap = new HashMap<>();
        List<String> clientNames = DatabaseHelper.executeQuery(
                "SELECT id, first_name, last_name FROM clients",
                rs -> {
                    String name = rs.getString("first_name") + " " + rs.getString("last_name");
                    clientMap.put(name, rs.getInt("id"));
                    return name;
                }
        );

        ComboBox<String> clientCombo = new ComboBox<>();
        clientCombo.getItems().addAll(clientNames);

        // -------- CHAMBRES DISPONIBLES --------
        List<Integer> rooms = DatabaseHelper.executeQuery(
                "SELECT number FROM rooms WHERE is_available = 1",
                rs -> rs.getInt("number")
        );

        ComboBox<Integer> roomCombo = new ComboBox<>();
        roomCombo.getItems().addAll(rooms);

        // -------- DATES --------
        DatePicker startPicker = new DatePicker(
                existing != null ? existing.getStartDate() : LocalDate.now());
        DatePicker endPicker = new DatePicker(
                existing != null ? existing.getEndDate() : LocalDate.now());

        // -------- PRÉ-SÉLECTION --------
        if (existing != null) {
            for (Map.Entry<String, Integer> entry : clientMap.entrySet()) {
                if (entry.getValue() == existing.getClientId()) {
                    clientCombo.getSelectionModel().select(entry.getKey());
                    break;
                }
            }
            roomCombo.getSelectionModel().select(existing.getRoomNumber());
        } else {
            if (!clientCombo.getItems().isEmpty())
                clientCombo.getSelectionModel().selectFirst();
            if (!roomCombo.getItems().isEmpty())
                roomCombo.getSelectionModel().selectFirst();
        }

        VBox content = new VBox(10,
                new Label("Client"), clientCombo,
                new Label("Chambre"), roomCombo,
                new Label("Date début"), startPicker,
                new Label("Date fin"), endPicker
        );
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String clientName = clientCombo.getValue();
                Integer room = roomCombo.getValue();

                if (clientName == null || room == null ||
                        startPicker.getValue() == null || endPicker.getValue() == null) {
                    showAlert("Tous les champs sont obligatoires.");
                    return null;
                }

                int clientId = clientMap.get(clientName);
                return new ReservationFormData(
                        clientId, room, startPicker.getValue(), endPicker.getValue());
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    // ================= CLASSE INTERNE =================
    private static class ReservationFormData {
        int clientId;
        int roomNumber;
        LocalDate startDate;
        LocalDate endDate;

        ReservationFormData(int c, int r, LocalDate s, LocalDate e) {
            clientId = c;
            roomNumber = r;
            startDate = s;
            endDate = e;
        }
    }

    // ================= ALERTES =================
    private static void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private static boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg,
                ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
