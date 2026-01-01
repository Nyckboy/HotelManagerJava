package com.hotel.project.frontend.screens;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardScreen {

  public static Scene getScene() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Label welcome = new Label("Welcome, " + AuthService.currentUser.getUsername());
    welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    layout.getChildren().addAll(welcome);

    // Gestion des utilisateurs (si manager)
    if (AuthService.currentUser.getRole().equals("MANAGER")) {
      Button newAccountBtn = new Button("Create New User");
      newAccountBtn.setOnAction(e -> {
        ScreenManager.setScene(CreateAccountScreen.getScene());
      });
      layout.getChildren().addAll(newAccountBtn);
    }

    // Gestion des chambres
    Button roomsBtn = new Button("Manage Rooms");
    roomsBtn.setOnAction(e -> {
      ScreenManager.setScene(RoomScreen.getScene());
      ScreenManager.setTitle("Manage Rooms");
    });

    // Gestion des réservations
    Button reservationBtn = new Button("📘 Réservations");
    reservationBtn.setOnAction(e -> {
      ScreenManager.setScene(ReservationScreen.getScene());
      ScreenManager.setTitle("Manage Reservations");
    });

    // Bouton de logout
    Button logoutBtn = new Button("Logout [→");
    logoutBtn.setOnAction(e -> {
      AuthService.logout();
      ScreenManager.setScene(LoginScreen.getScene());
    });

    layout.getChildren().addAll(roomsBtn, reservationBtn, logoutBtn);

    return new Scene(layout, 400, 450);
  }
}
