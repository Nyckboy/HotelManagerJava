package com.hotel.project.frontend.screens;

import com.hotel.project.backend.models.Room;
import com.hotel.project.backend.services.RoomService;
import com.hotel.project.frontend.ScreenManager;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    
    // Common styles
    private final String INPUT_STYLE = "-fx-background-radius: 5; -fx-padding: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 5;";
    private final String BTN_STYLE_BASE = "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

    public static Scene getScene() {
        RoomScreen screen = new RoomScreen();
        return screen.buildScene();
    }

    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");

        // --- 1. HEADER ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;");
        
        Label title = new Label("🛏️ ROOM MANAGER");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);

        // --- 2. CENTER: TOOLBAR & TABLE ---
        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));

        // Toolbar (Filters & Export)
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        
        Button refreshAllBtn = createButton("Show All", "#3498db");
        Button refreshAvailableBtn = createButton("Show Available Only", "#2ecc71");
        Button exportCsvBtn = createButton("📥 Export CSV", "#9b59b6");

        toolbar.getChildren().addAll(refreshAllBtn, refreshAvailableBtn, exportCsvBtn);

        // Table Setup
        setupTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        
        // Wrap Table in a Card
        VBox tableCard = new VBox(10, toolbar, table);
        tableCard.setPadding(new Insets(15));
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        centerLayout.getChildren().add(tableCard);
        root.setCenter(centerLayout);

        // --- 3. BOTTOM: MANAGEMENT CONSOLE ---
        HBox bottomConsole = new HBox(20);
        bottomConsole.setPadding(new Insets(20));
        bottomConsole.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        bottomConsole.setAlignment(Pos.CENTER_LEFT);

        // SECTION A: ADD ROOM FORM
        VBox addSection = new VBox(10);
        Label addLabel = new Label("Add New Room");
        addLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        HBox addInputs = new HBox(10);
        TextField numberField = styledTextField("Number");
        numberField.setPrefWidth(80);
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Single", "Double", "Twin", "Suite", "Deluxe", "Family");
        typeComboBox.setPromptText("Type");
        typeComboBox.setStyle(INPUT_STYLE);
        
        TextField priceField = styledTextField("Price");
        priceField.setPrefWidth(80);
        
        Button addBtn = createButton("➕ Add", "#27ae60");
        
        addInputs.getChildren().addAll(numberField, typeComboBox, priceField, addBtn);
        addSection.getChildren().addAll(addLabel, addInputs);

        // SECTION B: QUICK STATUS UPDATE
        VBox updateSection = new VBox(10);
        Label updateLabel = new Label("Quick Status Update");
        updateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        HBox updateInputs = new HBox(10);
        TextField updateIdField = styledTextField("Room #");
        updateIdField.setPrefWidth(80);
        
        CheckBox freeCheck = new CheckBox("Available");
        freeCheck.setSelected(true);
        freeCheck.setPadding(new Insets(5, 0, 0, 0));
        
        Button updateBtn = createButton("Update", "#e67e22");
        
        updateInputs.getChildren().addAll(updateIdField, freeCheck, updateBtn);
        updateSection.getChildren().addAll(updateLabel, updateInputs);

        // NAVIGATION BUTTON
        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        backBtn.setPrefHeight(35);
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));

        // Status Label
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 0 0 0;");

        // Combine Bottom Sections
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        bottomConsole.getChildren().addAll(addSection, new Separator(javafx.geometry.Orientation.VERTICAL), updateSection, spacer, statusLabel, backBtn);
        root.setBottom(bottomConsole);

        // --- LOGIC: BUTTON ACTIONS ---
        
        // 1. Refresh Logic
        refreshAllBtn.setOnAction(e -> {
            refreshTable();
            showInfo("Showing all rooms");
        });

        refreshAvailableBtn.setOnAction(e -> {
            Platform.runLater(() -> {
                var list = RoomService.getAvailableRooms();
                table.getItems().setAll(list);
                showInfo("Showing " + list.size() + " available room(s)");
            });
        });

        exportCsvBtn.setOnAction(e -> exportToCsv());

        // 2. Add Room Logic
        addBtn.setOnAction(e -> {
            if (numberField.getText().isEmpty() || typeComboBox.getValue() == null || priceField.getText().isEmpty()) {
                showError("Error: All fields are required!");
                return;
            }
            try {
                int num = Integer.parseInt(numberField.getText());
                double price = Double.parseDouble(priceField.getText());
                String type = typeComboBox.getValue();

                if (num <= 0 || price < 0) { showError("Values must be positive!"); return; }
                if (RoomService.roomExists(num)) { showError("Room " + num + " already exists!"); return; }

                if (RoomService.createRoom(num, type, price)) {
                    showSuccess("Room " + num + " added!");
                    numberField.clear(); typeComboBox.setValue(null); priceField.clear();
                    refreshTable();
                } else {
                    showError("Database Error.");
                }
            } catch (NumberFormatException ex) {
                showError("Invalid numbers!");
            }
        });

        // 3. Update Status Logic
        updateBtn.setOnAction(e -> {
            if (updateIdField.getText().isEmpty()) { showError("Room number required!"); return; }
            try {
                int num = Integer.parseInt(updateIdField.getText());
                boolean free = freeCheck.isSelected();
                
                if (!RoomService.roomExists(num)) { showError("Room not found!"); return; }
                
                if (RoomService.updateRoomStatus(num, free)) {
                    showSuccess("Room " + num + " is now " + (free ? "Available" : "Occupied"));
                    updateIdField.clear();
                    refreshTable();
                } else {
                    showError("Update Failed.");
                }
            } catch (NumberFormatException ex) {
                showError("Invalid Room Number!");
            }
        });

        refreshTable(); // Load initial data
        return new Scene(root, 900, 650); // Wider screen for better layout
    }

    // --- HELPER METHODS ---

    private void setupTable() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: white; -fx-border-color: #eee;");

        TableColumn<Room, Integer> c1 = new TableColumn<>("Number");
        c1.setCellValueFactory(data -> data.getValue().numberProperty().asObject());

        TableColumn<Room, String> c2 = new TableColumn<>("Type");
        c2.setCellValueFactory(data -> data.getValue().typeProperty());

        TableColumn<Room, String> c3 = new TableColumn<>("Price");
        c3.setCellValueFactory(data -> data.getValue().priceProperty().asString("$%.2f"));

        TableColumn<Room, String> c4 = new TableColumn<>("Status");
        c4.setCellValueFactory(data -> data.getValue().isAvailableProperty().asString());
        c4.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    boolean isFree = item.equals("true");
                    setText(isFree ? "Available" : "Occupied");
                    setTextFill(isFree ? Color.GREEN : Color.RED);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Room, Void> c5 = new TableColumn<>("Actions");
        c5.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✎ Edit");
            {
                editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand;");
                editBtn.setOnAction(event -> {
                    Room room = getTableView().getItems().get(getIndex());
                    if (room != null) showEditDialog(room);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        table.getColumns().addAll(c1, c2, c3, c4, c5);
    }

    private void showEditDialog(Room room) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Room " + room.getNumber());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Edit Room " + room.getNumber());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Single", "Double", "Twin", "Suite", "Deluxe", "Family");
        typeBox.setValue(room.getType());
        typeBox.setStyle(INPUT_STYLE);
        typeBox.setPrefWidth(200);

        TextField priceField = styledTextField("Price");
        priceField.setText(String.valueOf(room.getPrice()));

        CheckBox availCheck = new CheckBox("Is Available");
        availCheck.setSelected(room.isAvailable());

        Button saveBtn = createButton("Save Changes", "#27ae60");
        saveBtn.setOnAction(e -> {
            try {
                double p = Double.parseDouble(priceField.getText());
                if (p < 0) return;
                
                if (RoomService.updateRoom(room.getNumber(), typeBox.getValue(), p, availCheck.isSelected())) {
                    refreshTable();
                    dialog.close();
                    showSuccess("Room Updated!");
                }
            } catch (Exception ex) { /* Handle error */ }
        });

        layout.getChildren().addAll(title, new Label("Type:"), typeBox, new Label("Price:"), priceField, availCheck, saveBtn);
        dialog.setScene(new Scene(layout, 300, 350));
        dialog.showAndWait();
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(INPUT_STYLE);
        return tf;
    }

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; " + BTN_STYLE_BASE);
        return btn;
    }

    private void refreshTable() {
        Platform.runLater(() -> table.getItems().setAll(RoomService.getAllRooms()));
    }

    private void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("rooms.csv");
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());
        
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("Number,Type,Price,Available\n");
                for (Room r : RoomService.getAllRooms()) {
                    writer.append(r.getNumber() + "," + r.getType() + "," + r.getPrice() + "," + (r.isAvailable() ? "Yes" : "No") + "\n");
                }
                showSuccess("Exported to " + file.getName());
            } catch (IOException e) { showError("Export Failed"); }
        }
    }

    private void showError(String msg) { statusLabel.setText("❌ " + msg); statusLabel.setTextFill(Color.RED); }
    private void showSuccess(String msg) { statusLabel.setText("✅ " + msg); statusLabel.setTextFill(Color.GREEN); }
    private void showInfo(String msg) { statusLabel.setText("ℹ️ " + msg); statusLabel.setTextFill(Color.BLUE); }
}
