package com.hotel.project.frontend.screens;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DashboardScreen {

  public static Scene getScene() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label welcome = new Label("Welcome, " + AuthService.currentUser.getUsername());
    welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    layout.getChildren().addAll(welcome);

    if (AuthService.currentUser.getRole().equals("MANAGER")) {
      Button newAccountBtn = new Button("Create New User");
      newAccountBtn.setOnAction(e -> {
        ScreenManager.setScene(CreateAccountScreen.getScene());
      });
      layout.getChildren().addAll(newAccountBtn);
    }

    Button roomsBtn = new Button("Manage Rooms");
    roomsBtn.setOnAction(e -> {
      // TODO we change the scene to the room's scene when it s ready
      // ScreenManager.setScreen();
    });
    Button logoutBtn = new Button("Logout [→");
    logoutBtn.setOnAction(e -> {
      AuthService.logout();
      ScreenManager.setScene(LoginScreen.getScene());
    });
    layout.getChildren().addAll(roomsBtn, logoutBtn);
    return new Scene(layout, 400, 400);
  }
}
