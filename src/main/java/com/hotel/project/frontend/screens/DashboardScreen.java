package com.hotel.project.frontend.screens;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardScreen {

  public static Scene getScene() {
    // --- 1. MAIN LAYOUT (BorderPane) ---
    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: #f4f4f4;"); // Light gray background

    // --- 2. HEADER SECTION ---
    VBox header = new VBox(10);
    header.setPadding(new Insets(30, 20, 30, 20));
    header.setAlignment(Pos.CENTER);
    header.setStyle("-fx-background-color: #2c3e50;"); // Dark Blue Header

    Label appTitle = new Label("HOTEL MANAGER PRO");
    appTitle.setTextFill(Color.WHITE);
    appTitle.setFont(new Font("Arial", 24));
    appTitle.setStyle("-fx-font-weight: bold;");

    Label welcome = new Label("Welcome back, " + AuthService.currentUser.getUsername());
    welcome.setTextFill(Color.LIGHTGRAY);
    welcome.setFont(new Font("Arial", 14));

    header.getChildren().addAll(appTitle, welcome);
    root.setTop(header);

    // --- 3. MENU GRID (The Center) ---
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(20);
    grid.setVgap(20);
    grid.setPadding(new Insets(40));

    // Create Styled Buttons (Cards)
    Button roomsBtn = createMenuButton("🛏️ Rooms", "#3498db");
    roomsBtn.setOnAction(e -> {
      ScreenManager.setScene(RoomScreen.getScene());
      ScreenManager.setTitle("Manage Rooms");
    });

    Button clientBtn = createMenuButton("👥 Clients", "#9b59b6");
    clientBtn.setOnAction(e -> ScreenManager.setScene(ClientScreen.getScene()));

    Button reservationBtn = createMenuButton("📅 Bookings", "#e67e22");
    reservationBtn.setOnAction(e -> {
      ScreenManager.setScene(ReservationScreen.getScene());
      ScreenManager.setTitle("Manage Reservations");
    });

    Button financeBtn = createMenuButton("💰 Finance", "#27ae60");
    financeBtn.setOnAction(e -> ScreenManager.setScene(FinanceScreen.getScene()));

    // Add them to the grid (Row 0)
    grid.add(roomsBtn, 0, 0);
    grid.add(clientBtn, 1, 0);
    grid.add(reservationBtn, 0, 1);
    grid.add(financeBtn, 1, 1);

    // --- 4. MANAGER ONLY SECTION ---
    if (AuthService.currentUser.getRole().equals("MANAGER")) {
      Button adminBtn = createMenuButton("👥 Manage Staff", "#c0392b");
      adminBtn.setOnAction(e -> ScreenManager.setScene(CreateAccountScreen.getScene()));
      grid.add(adminBtn, 0, 2, 2, 1); // Span across 2 columns
      adminBtn.setMaxWidth(Double.MAX_VALUE); // Fill width
    }

    root.setCenter(grid);

    // --- 5. FOOTER (Logout) ---
    HBox footer = new HBox();
    footer.setAlignment(Pos.CENTER);
    footer.setPadding(new Insets(20));

    Button logoutBtn = new Button("Logout");
    logoutBtn.setStyle(
        "-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #7f8c8d; -fx-border-radius: 5;");
    logoutBtn.setPrefWidth(100);
    logoutBtn.setOnAction(e -> {
      AuthService.logout();
      ScreenManager.setScene(LoginScreen.getScene());
    });

    footer.getChildren().add(logoutBtn);
    root.setBottom(footer);

    return new Scene(root, 600, 600);
  }

  // --- HELPER TO CREATE STYLED BUTTONS ---
  private static Button createMenuButton(String text, String colorHex) {
    Button btn = new Button(text);
    btn.setPrefSize(180, 100); // Big buttons
    btn.setFont(new Font("Arial", 16));

    // CSS Styling for "Card" look
    String style = "-fx-background-color: " + colorHex + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 10;" +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"; // Shadow

    btn.setStyle(style);

    // Add Hover Effect
    btn.setOnMouseEntered(e -> btn.setStyle(style + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;")); // Zoom in
    btn.setOnMouseExited(e -> btn.setStyle(style + "-fx-scale-x: 1.0; -fx-scale-y: 1.0;")); // Zoom out

    return btn;
  }
}
