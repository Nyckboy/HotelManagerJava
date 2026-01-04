package com.hotel.project.frontend.screens;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LoginScreen {

    public static Scene getScene() {
        // --- 1. BACKGROUND ---
        // Using a StackPane to allow a full-screen background color
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);");

        // --- 2. LOGIN CARD (The White Box) ---
        VBox card = new VBox(20); // Spacing between elements
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(350);
        card.setMaxHeight(400);
        card.setPadding(new Insets(40));
        
        // CSS for the Card (White, Rounded, Shadow)
        card.setStyle("-fx-background-color: white;" + 
                      "-fx-background-radius: 15;" +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        // --- 3. UI ELEMENTS ---
        
        // Icon/Title
        Label icon = new Label("🏨");
        icon.setStyle("-fx-font-size: 50px;");
        
        Label title = new Label("HOTEL LOGIN");
        title.setFont(new Font("Arial", 22));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        // Input Fields with Styling
        TextField userField = new TextField("admin"); // Pre-filled for testing
        userField.setPromptText("Username");
        userField.setStyle("-fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

        PasswordField userPassword = new PasswordField();
        userPassword.setPromptText("Password");
        userPassword.setStyle("-fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

        // Error Message
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Login Button
        Button loginBtn = new Button("SIGN IN");
        loginBtn.setPrefWidth(200);
        loginBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10;");
        
        // Button Hover Effect
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10;"));

        // --- 4. LOGIC ---
        loginBtn.setOnAction(e -> {
            if (AuthService.login(userField.getText(), userPassword.getText())) {
                ScreenManager.setScene(DashboardScreen.getScene());
            } else {
                errorLabel.setText("Invalid Username or Password");
                // Shake animation logic could go here, but let's keep it simple
            }
        });

        // Allow pressing "Enter" key to login
        userPassword.setOnAction(e -> loginBtn.fire());

        // --- 5. ASSEMBLE ---
        card.getChildren().addAll(icon, title, new Separator(), userField, userPassword, loginBtn, errorLabel);
        root.getChildren().add(card);

        return new Scene(root, 600, 500);
    }
}
