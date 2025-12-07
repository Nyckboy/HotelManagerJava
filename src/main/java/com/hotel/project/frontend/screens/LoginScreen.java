package com.hotel.project.frontend.screens;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginScreen {

  public static Scene getScene() {
    VBox layout = new VBox(15);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(20));

    Label title = new Label("Hotel Login");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    TextField userFeild = new TextField("admin");
    PasswordField userPassword = new PasswordField();
    Button loginBtn = new Button("Login");
    Label errorLabel = new Label();

    loginBtn.setOnAction(e -> {
      if (AuthService.login(userFeild.getText(), userPassword.getText())) {
        ScreenManager.setScene(DashboardScreen.getScene());
      } else {
        errorLabel.setText("Wrong Username/Password");
        errorLabel.setTextFill(Color.RED);
      }
    });

    layout.getChildren().addAll(title, userFeild, userPassword, loginBtn, errorLabel);
    return new Scene(layout, 300, 300);
  }
}
