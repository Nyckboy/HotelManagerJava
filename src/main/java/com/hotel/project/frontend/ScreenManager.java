package com.hotel.project.frontend;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
  private static Stage stage;

  // THIS IS ONLY CALLED ONCE IN MAIN
  public static void init(Stage primaryStage) {
    stage = primaryStage;
  }

  // CALL THIS TO CHANGE THE PAGE !!
  public static void setScene(Scene scene) {
    stage.setScene(scene);
    stage.show();
  }

  // THIS IF FOR TITLE CHANGE IN EACH PAGE
  public static void setTitle(String title) {
    stage.setTitle(title);
  }
}
