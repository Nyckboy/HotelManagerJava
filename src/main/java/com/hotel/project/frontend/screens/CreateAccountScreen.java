package com.hotel.project.frontend.screens;

import com.hotel.project.backend.models.Compte;
import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CreateAccountScreen {

  public static Scene getScene() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(20));

    Label welcome = new Label("Welcome, " + AuthService.currentUser.getUsername());
    welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Label header = new Label("--- Create New Account ---");
    TextField newUser = new TextField();
    newUser.setPromptText("New Username");

    TextField newPass = new TextField();
    newPass.setPromptText("Password");

    ComboBox<String> roleBox = new ComboBox<>();
    roleBox.getItems().addAll("MANAGER", "RECEPTIONIST");
    roleBox.setValue("RECEPTIONIST");

    Button createBtn = new Button("Create User");
    Label msgLabel = new Label("");
    createBtn.setOnAction(e -> {
      AuthService.register(newUser.getText(), newPass.getText(), roleBox.getValue());
      msgLabel.setText("User Created !");
      msgLabel.setTextFill(Color.GREEN);
    });

    Button backBtn = new Button("← Back");
    backBtn.setOnAction(e -> {
      ScreenManager.setScene(DashboardScreen.getScene());
    });

    layout.getChildren().addAll(welcome, header, newUser, newPass, roleBox, createBtn, msgLabel, backBtn);

    return new Scene(layout, 400, 400);

  }
}
