package com.hotel.project;

import com.hotel.project.backend.services.AuthService;
import com.hotel.project.frontend.ScreenManager;
import com.hotel.project.frontend.screens.LoginScreen;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) {
    com.hotel.project.backend.DatabaseConnection.ConnectTest();
    AuthService.init();

    ScreenManager.init(primaryStage);
    primaryStage.setTitle("Hotel System");

    ScreenManager.setScene(LoginScreen.getScene());
  }

  public static void main(String[] args) {
    launch(args);
  }
}
