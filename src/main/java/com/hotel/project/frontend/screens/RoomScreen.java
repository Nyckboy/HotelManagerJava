package com.hotel.project.frontend.screens;

import com.hotel.project.backend.models.Room;
import com.hotel.project.backend.services.RoomService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RoomScreen {

    private TableView<Room> table;
    private Label statusLabel;

    public static Scene getScene() {
        RoomScreen screen = new RoomScreen();
        return screen.buildScene();
    }

    private Scene buildScene() {
        // ---- Title ----
        Label title = new Label("Room Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // ---- Form: Add Room ----
        TextField numberField = new TextField();
        numberField.setPromptText("Room Number");

        // ComboBox pour le type de chambre avec choix prédéfinis
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Single", "Double", "Twin", "Suite", "Deluxe", "Family");
        typeComboBox.setPromptText("Select Room Type");
        typeComboBox.setPrefWidth(200);

        TextField priceField = new TextField();
        priceField.setPromptText("Price per night");

        Button addBtn = new Button("Add Room");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            // Validation des champs
            if (numberField.getText().isEmpty() || 
                typeComboBox.getValue() == null || 
                priceField.getText().isEmpty()) {
                showError("Error: All fields are required!");
                return;
            }

            try {
                int num = Integer.parseInt(numberField.getText());
                double price = Double.parseDouble(priceField.getText());
                String type = typeComboBox.getValue();

                // Validation métier
                if (num <= 0) {
                    showError("Error: Room number must be positive!");
                    return;
                }

                if (price < 0) {
                    showError("Error: Price cannot be negative!");
                    return;
                }

                // Vérifier si la chambre existe déjà
                if (RoomService.roomExists(num)) {
                    showError("Error: Room " + num + " already exists!");
                    return;
                }

                // Créer la chambre
                if (RoomService.createRoom(num, type, price)) {
                    showSuccess("Room " + num + " added successfully!");
                    // Vider les champs
                    numberField.clear();
                    typeComboBox.setValue(null);
                    priceField.clear();
                    // Rafraîchir la table
                    refreshTable();
                } else {
                    showError("Error: Failed to add room. Check console for details.");
                }
            } catch (NumberFormatException ex) {
                showError("Error: Invalid number format! Room number and price must be numbers.");
            }
        });

        VBox form = new VBox(10, numberField, typeComboBox, priceField, addBtn, statusLabel);
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER);

        // ---- Table: List rooms ----
        table = new TableView<>();
        
        TableColumn<Room, Integer> c1 = new TableColumn<>("Number");
        c1.setCellValueFactory(data -> data.getValue().numberProperty().asObject());
        c1.setPrefWidth(80);

        TableColumn<Room, String> c2 = new TableColumn<>("Type");
        c2.setCellValueFactory(data -> data.getValue().typeProperty());
        c2.setPrefWidth(150);

        TableColumn<Room, Double> c3 = new TableColumn<>("Price");
        c3.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        c3.setPrefWidth(100);
        // Formater le prix avec 2 décimales
        c3.setCellFactory(column -> new TableCell<Room, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", price));
                }
            }
        });

        TableColumn<Room, Boolean> c4 = new TableColumn<>("Available");
        c4.setCellValueFactory(data -> data.getValue().isAvailableProperty().asObject());
        c4.setPrefWidth(100);

        // Colonne Actions avec bouton Modifier
        TableColumn<Room, Void> c5 = new TableColumn<>("Actions");
        c5.setPrefWidth(100);
        c5.setCellFactory(param -> new TableCell<Room, Void>() {
            private final Button editBtn = new Button("Modifier");
            
            {
                editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10px;");
                editBtn.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    if (room != null) {
                        showEditDialog(room);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editBtn);
                }
            }
        });

        table.getColumns().addAll(c1, c2, c3, c4, c5);
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Boutons pour la table
        Button refreshAllBtn = new Button("Show All Rooms");
        refreshAllBtn.setOnAction(e -> {
            refreshTable();
            showInfo("Showing all rooms");
        });

        Button refreshAvailableBtn = new Button("Show Available Rooms Only");
        refreshAvailableBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        refreshAvailableBtn.setOnAction(e -> {
            Platform.runLater(() -> {
                var list = RoomService.getAvailableRooms();
                table.getItems().setAll(list);
                showInfo("Showing " + list.size() + " available room(s)");
            });
        });

        Button exportCsvBtn = new Button("Export to CSV");
        exportCsvBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        exportCsvBtn.setOnAction(e -> exportToCsv());

        HBox tableButtons = new HBox(10, refreshAllBtn, refreshAvailableBtn, exportCsvBtn);
        tableButtons.setAlignment(Pos.CENTER);

        VBox tableBox = new VBox(10, tableButtons, table);
        tableBox.setPadding(new Insets(10));
        tableBox.setAlignment(Pos.CENTER);

        // ---- Update Room Status ----
        Label updateLabel = new Label("Update Room Status");
        updateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField roomIdField = new TextField();
        roomIdField.setPromptText("Room number");

        CheckBox freeCheck = new CheckBox("Is Available");
        freeCheck.setSelected(true);

        Button updateBtn = new Button("Update Status");
        updateBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        updateBtn.setOnAction(e -> {
            if (roomIdField.getText().isEmpty()) {
                showError("Error: Room number is required!");
                return;
            }

            try {
                int num = Integer.parseInt(roomIdField.getText());
                boolean free = freeCheck.isSelected();

                if (!RoomService.roomExists(num)) {
                    showError("Error: Room " + num + " does not exist!");
                    return;
                }

                if (RoomService.updateRoomStatus(num, free)) {
                    showSuccess("Room " + num + " status updated to " + (free ? "available" : "occupied"));
                    roomIdField.clear();
                    refreshTable();
                } else {
                    showError("Error: Failed to update room status!");
                }
            } catch (NumberFormatException ex) {
                showError("Error: Room number must be a valid integer!");
            }
        });

        VBox updateBox = new VBox(10, updateLabel, roomIdField, freeCheck, updateBtn);
        updateBox.setAlignment(Pos.CENTER);
        updateBox.setPadding(new Insets(10));

        // ---- Main Layout ----
        VBox root = new VBox(20, title, form, tableBox, updateBox);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Charger les chambres au démarrage
        refreshTable();

        return new Scene(root, 700, 650);
    }

    private void refreshTable() {
        Platform.runLater(() -> {
            var list = RoomService.getAllRooms();
            table.getItems().setAll(list);
            if (list.isEmpty()) {
                showInfo("No rooms found. Add your first room!");
            }
        });
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Color.RED);
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Color.GREEN);
    }

    private void showInfo(String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(Color.BLUE);
    }

    private void showEditDialog(Room room) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Modifier la chambre " + room.getNumber());
        dialog.setResizable(false);

        Label title = new Label("Modifier la chambre " + room.getNumber());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Le numéro de chambre ne peut pas être modifié (clé primaire)
        Label numberLabel = new Label("Numéro: " + room.getNumber());
        numberLabel.setStyle("-fx-font-size: 12px;");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Single", "Double", "Twin", "Suite", "Deluxe", "Family");
        typeComboBox.setValue(room.getType());
        typeComboBox.setPrefWidth(200);

        TextField priceField = new TextField();
        priceField.setText(String.valueOf(room.getPrice()));
        priceField.setPromptText("Prix par nuit");

        CheckBox availableCheckBox = new CheckBox("Disponible");
        availableCheckBox.setSelected(room.isAvailable());

        Label dialogStatusLabel = new Label("");
        dialogStatusLabel.setStyle("-fx-font-size: 11px;");

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> {
            if (typeComboBox.getValue() == null || priceField.getText().isEmpty()) {
                dialogStatusLabel.setText("Erreur: Tous les champs sont requis!");
                dialogStatusLabel.setTextFill(Color.RED);
                return;
            }

            try {
                double price = Double.parseDouble(priceField.getText());
                if (price < 0) {
                    dialogStatusLabel.setText("Erreur: Le prix ne peut pas être négatif!");
                    dialogStatusLabel.setTextFill(Color.RED);
                    return;
                }

                String type = typeComboBox.getValue();
                boolean available = availableCheckBox.isSelected();
                if (RoomService.updateRoom(room.getNumber(), type, price, available)) {
                    showSuccess("Chambre " + room.getNumber() + " modifiée avec succès!");
                    refreshTable();
                    dialog.close();
                } else {
                    dialogStatusLabel.setText("Erreur: Échec de la modification!");
                    dialogStatusLabel.setTextFill(Color.RED);
                }
            } catch (NumberFormatException ex) {
                dialogStatusLabel.setText("Erreur: Format de prix invalide!");
                dialogStatusLabel.setTextFill(Color.RED);
            }
        });

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox dialogContent = new VBox(15, title, numberLabel, typeComboBox, priceField, availableCheckBox, buttons, dialogStatusLabel);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogContent, 350, 280);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les chambres en CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("rooms_export.csv");

        Stage stage = (Stage) table.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                List<Room> rooms = RoomService.getAllRooms();
                FileWriter writer = new FileWriter(file);

                // En-têtes CSV
                writer.append("Number,Type,Price,Available\n");

                // Données
                for (Room room : rooms) {
                    writer.append(String.valueOf(room.getNumber())).append(",");
                    writer.append(room.getType()).append(",");
                    writer.append(String.format("%.2f", room.getPrice())).append(",");
                    writer.append(room.isAvailable() ? "Yes" : "No").append("\n");
                }

                writer.flush();
                writer.close();

                showSuccess("Export CSV réussi! Fichier sauvegardé: " + file.getName());
            } catch (IOException ex) {
                showError("Erreur lors de l'export CSV: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}