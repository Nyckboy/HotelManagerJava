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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ClientScreen {

    public static Scene getScene() {
        // --- 1. LAYOUT SETUP ---
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("CLIENT MANAGEMENT");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // --- 2. TABLE VIEW (THE LIST) ---
        TableView<Client> table = new TableView<>();
        
        // Define Columns (Must match field names in Client.java)
        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Client, String> colFirst = new TableColumn<>("First Name");
        colFirst.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Client, String> colLast = new TableColumn<>("Last Name");
        colLast.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        
        TableColumn<Client, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Add columns to table
        table.getColumns().addAll(colId, colFirst, colLast, colPhone);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Fit width
        VBox.setVgrow(table, Priority.ALWAYS); // Make table expand

        // --- 3. INPUT FORM (ADD NEW) ---
        TextField fNameField = new TextField(); fNameField.setPromptText("First Name");
        TextField lNameField = new TextField(); lNameField.setPromptText("Last Name");
        TextField phoneField = new TextField(); phoneField.setPromptText("Phone Number");
        
        // Group inputs in a row
        HBox inputBox = new HBox(10, fNameField, lNameField, phoneField);
        inputBox.setAlignment(Pos.CENTER);

        Label msgLabel = new Label(); // For success/error messages

        // --- 4. BUTTONS & ACTIONS ---
        
        // REFRESH FUNCTION (Reloads list from DB)
        Runnable refreshTable = () -> {
            table.setItems(FXCollections.observableArrayList(ClientService.getAllClients()));
        };
        refreshTable.run(); // Load data immediately on start

        // ADD BUTTON
        Button addBtn = new Button("Add Client");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addBtn.setOnAction(e -> {
            String f = fNameField.getText();
            String l = lNameField.getText();
            String p = phoneField.getText();

            if (f.isEmpty() || l.isEmpty() || p.isEmpty()) {
                msgLabel.setText("Please fill all fields!");
                msgLabel.setTextFill(Color.RED);
                return;
            }

            // CALL BACKEND
            if (ClientService.addClient(f, l, p)) {
                msgLabel.setText("Client Added!");
                msgLabel.setTextFill(Color.GREEN);
                fNameField.clear(); lNameField.clear(); phoneField.clear();
                refreshTable.run(); // Update table
            } else {
                msgLabel.setText("Error Adding Client");
                msgLabel.setTextFill(Color.RED);
            }
        });

        // DELETE BUTTON
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            Client selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                msgLabel.setText("Select a client first!");
                msgLabel.setTextFill(Color.RED);
                return;
            }

            // CALL BACKEND
            if (ClientService.deleteClient(selected.id)) {
                msgLabel.setText("Deleted Client #" + selected.id);
                msgLabel.setTextFill(Color.BLACK);
                refreshTable.run();
            }
        });

        // BACK BUTTON
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));

        HBox buttonBox = new HBox(15, addBtn, deleteBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // --- 5. FINALIZE ---
        root.getChildren().addAll(title, table, inputBox, buttonBox, msgLabel);
        return new Scene(root, 600, 500);
    }
}