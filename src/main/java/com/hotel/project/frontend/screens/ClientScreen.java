package com.hotel.project.frontend.screens;

import com.hotel.project.backend.models.Client;
import com.hotel.project.backend.services.ClientService;
import com.hotel.project.frontend.ScreenManager;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ClientScreen {

    public static Scene getScene() {
        // --- 1. MAIN STRUCTURE ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;"); // Light Gray BG

        // --- 2. HEADER ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;"); // Dark Blue
        
        Label title = new Label("👥 CLIENT MANAGER");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);

        // --- 3. THE TABLE (Center) ---
        TableView<Client> table = new TableView<>();
        
        // Define Columns
        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Client, String> colFirst = new TableColumn<>("First Name");
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        
        TableColumn<Client, String> colLast = new TableColumn<>("Last Name");
        colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        
        TableColumn<Client, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        table.getColumns().addAll(colId, colFirst, colLast, colPhone);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Table Styling
        table.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        
        // Wrapper for margins
        VBox tableContainer = new VBox(table);
        tableContainer.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(tableContainer);

        // --- 4. CONTROL PANEL (Bottom Card) ---
        VBox controls = new VBox(15);
        controls.setPadding(new Insets(20));
        controls.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;"); // Top border only

        Label formTitle = new Label("Add New Client");
        formTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        // Input Fields (Styled)
        String inputStyle = "-fx-background-radius: 5; -fx-padding: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 5;";
        
        TextField fNameField = new TextField(); 
        fNameField.setPromptText("First Name");
        fNameField.setStyle(inputStyle);
        fNameField.setPrefHeight(35);
        HBox.setHgrow(fNameField, Priority.ALWAYS);

        TextField lNameField = new TextField(); 
        lNameField.setPromptText("Last Name");
        lNameField.setStyle(inputStyle);
        lNameField.setPrefHeight(35);
        HBox.setHgrow(lNameField, Priority.ALWAYS);

        TextField phoneField = new TextField(); 
        phoneField.setPromptText("Phone Number");
        phoneField.setStyle(inputStyle);
        phoneField.setPrefHeight(35);
        HBox.setHgrow(phoneField, Priority.ALWAYS);

        HBox inputsBox = new HBox(10, fNameField, lNameField, phoneField);
        inputsBox.setAlignment(Pos.CENTER_LEFT);

        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-font-weight: bold;");

        // --- BUTTONS ---
        
        // Refresh Logic
        Runnable refreshTable = () -> {
            table.setItems(FXCollections.observableArrayList(ClientService.getAllClients()));
        };
        refreshTable.run(); 

        Button addBtn = new Button("➕ Add Client");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        addBtn.setPrefHeight(35);
        addBtn.setPrefWidth(120);
        
        addBtn.setOnAction(e -> {
            String f = fNameField.getText();
            String l = lNameField.getText();
            String p = phoneField.getText();

            if (f.isEmpty() || l.isEmpty() || p.isEmpty()) {
                msgLabel.setText("Please fill all fields!");
                msgLabel.setTextFill(Color.RED);
                return;
            }

            if (ClientService.addClient(f, l, p)) {
                msgLabel.setText("✅ Client Added!");
                msgLabel.setTextFill(Color.GREEN);
                fNameField.clear(); lNameField.clear(); phoneField.clear();
                refreshTable.run();
            } else {
                msgLabel.setText("❌ Error Adding Client");
                msgLabel.setTextFill(Color.RED);
            }
        });

        Button deleteBtn = new Button("🗑 Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        deleteBtn.setPrefHeight(35);
        
        deleteBtn.setOnAction(e -> {
            Client selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                msgLabel.setText("Select a client to delete!");
                msgLabel.setTextFill(Color.RED);
                return;
            }
            if (ClientService.deleteClient(selected.id)) {
                msgLabel.setText("🗑 Deleted Client #" + selected.id);
                msgLabel.setTextFill(Color.BLACK);
                refreshTable.run();
            }
        });

        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        backBtn.setPrefHeight(35);
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));

        HBox actionBox = new HBox(15, addBtn, deleteBtn, new Separator(), backBtn);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        // Assemble Control Panel
        controls.getChildren().addAll(formTitle, inputsBox, actionBox, msgLabel);
        root.setBottom(controls);

        return new Scene(root, 700, 600);
    }
}
