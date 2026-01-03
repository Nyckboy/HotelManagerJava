package com.hotel.project.frontend.screens;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CreateAccountScreen {

    public static Scene getScene() {
        // --- 1. MAIN LAYOUT ---
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;"); // Light gray background
        root.setPadding(new Insets(20));

        // --- 2. THE FORM CARD ---
        VBox card = new VBox(15);
        card.setMaxWidth(400);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        
        // Card Styling (White, Rounded, Shadow)
        card.setStyle("-fx-background-color: white;" + 
                      "-fx-background-radius: 10;" +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // --- 3. UI ELEMENTS ---

        Label title = new Label("ADD NEW STAFF");
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Manager: " + AuthService.currentUser.getUsername());
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        // Input Styling
        String inputStyle = "-fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 8;";

        TextField newUser = new TextField();
        newUser.setPromptText("Username");
        newUser.setStyle(inputStyle);
        newUser.setPrefHeight(40);

        TextField newPass = new TextField(); // Or PasswordField if you prefer
        newPass.setPromptText("Password");
        newPass.setStyle(inputStyle);
        newPass.setPrefHeight(40);

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("MANAGER", "RECEPTIONIST");
        roleBox.setValue("RECEPTIONIST");
        roleBox.setStyle(inputStyle);
        roleBox.setPrefWidth(400); // Full width
        roleBox.setPrefHeight(40);

        // Success/Error Message
        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-font-size: 13px;");

        // --- 4. BUTTONS ---
        
        // "Create" Button (Green)
        Button createBtn = new Button("Create Account");
        createBtn.setPrefWidth(400);
        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10;");
        
        createBtn.setOnAction(e -> {
            String u = newUser.getText();
            String p = newPass.getText();
            String r = roleBox.getValue();

            if(u.isEmpty() || p.isEmpty()) {
                msgLabel.setText("Please fill all fields!");
                msgLabel.setTextFill(Color.RED);
                return;
            }

            if (AuthService.register(u, p, r)) {
                msgLabel.setText("✅ User '" + u + "' Created Successfully!");
                msgLabel.setTextFill(Color.GREEN);
                newUser.clear();
                newPass.clear();
            } else {
                msgLabel.setText("❌ User already exists!");
                msgLabel.setTextFill(Color.RED);
            }
        });

        // "Back" Button (Simple Grey)
        Button backBtn = new Button("Cancel / Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        backBtn.setPrefWidth(400);
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));

        // --- 5. ASSEMBLE ---
        card.getChildren().addAll(title, subtitle, new Separator(), 
                                  new Label("Username"), newUser, 
                                  new Label("Password"), newPass, 
                                  new Label("Role"), roleBox, 
                                  new Separator(), createBtn, backBtn, msgLabel);

        root.getChildren().add(card);

        return new Scene(root, 600, 600);
    }
}
