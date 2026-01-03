package com.hotel.project.frontend.screens;

import com.hotel.project.backend.models.Paiement;
import com.hotel.project.backend.models.Reservation;
import com.hotel.project.backend.services.FinanceService;
import com.hotel.project.frontend.ScreenManager;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDate;

public class FinanceScreen {

    private static int selectedResId = -1;
    private static final String INPUT_STYLE = "-fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 5;";

    public static Scene getScene() {
        // --- 1. MAIN STRUCTURE ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f4f4;");

        // --- 2. HEADER ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2c3e50;");
        
        Label title = new Label("💰 FINANCE & BILLING");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("Arial", 20));
        title.setStyle("-fx-font-weight: bold;");
        
        header.getChildren().add(title);
        root.setTop(header);

        // --- 3. CENTER CONTENT (TABS) ---
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        // ============================================
        // TAB 1: PENDING BILLS (DEBTORS)
        // ============================================
        
        // A. The Table (Left Side)
        TableView<Reservation> debtTable = new TableView<>();
        debtTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Reservation, Integer> colResId = new TableColumn<>("ID");
        colResId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colResId.setPrefWidth(50);
        
        TableColumn<Reservation, String> colClientName = new TableColumn<>("Client");
        colClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        TableColumn<Reservation, Integer> colRoom = new TableColumn<>("Room");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        debtTable.getColumns().addAll(colResId, colClientName, colRoom);

        // B. The Payment Card (Right Side)
        VBox payCard = new VBox(15);
        payCard.setPrefWidth(300);
        payCard.setPadding(new Insets(20));
        payCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        payCard.setAlignment(Pos.TOP_CENTER);

        Label cardTitle = new Label("💳 Process Payment");
        cardTitle.setFont(new Font("Arial", 16));
        cardTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label infoLabel = new Label("Select a reservation from the list...");
        infoLabel.setWrapText(true);
        infoLabel.setStyle("-fx-text-alignment: center; -fx-text-fill: #7f8c8d;");
        infoLabel.setPadding(new Insets(10, 0, 10, 0));

        TextField amountField = new TextField(); 
        amountField.setPromptText("Enter Amount ($)");
        amountField.setStyle(INPUT_STYLE);
        amountField.setDisable(true);

        Button payBtn = new Button("Confirm Payment");
        payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10;");
        payBtn.setMaxWidth(Double.MAX_VALUE);
        payBtn.setDisable(true);
        
        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold;");

        payCard.getChildren().addAll(cardTitle, new Separator(), infoLabel, amountField, payBtn, statusMsg);

        // Layout for Tab 1
        HBox tab1Layout = new HBox(20, debtTable, payCard);
        tab1Layout.setPadding(new Insets(20));
        HBox.setHgrow(debtTable, Priority.ALWAYS);
        
        Tab tab1 = new Tab("⚠️ Pending Bills", tab1Layout);
        tab1.setClosable(false);

        // ============================================
        // TAB 2: PAYMENT HISTORY (PAID LIST)
        // ============================================
        TableView<Paiement> historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Paiement, Integer> colPayId = new TableColumn<>("Payment ID");
        colPayId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Paiement, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); } 
                else { setText(String.format("$%.2f", item)); setTextFill(Color.GREEN); setStyle("-fx-font-weight: bold;"); }
            }
        });

        TableColumn<Paiement, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Paiement, Integer> colResRef = new TableColumn<>("Reservation Ref#");
        colResRef.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        historyTable.getColumns().addAll(colPayId, colAmount, colDate, colResRef);
        
        VBox tab2Layout = new VBox(historyTable);
        tab2Layout.setPadding(new Insets(20));
        
        Tab tab2 = new Tab("📜 Payment History", tab2Layout);
        tab2.setClosable(false);

        // Add Tabs
        tabPane.getTabs().addAll(tab1, tab2);
        root.setCenter(tabPane);

        // --- 4. BOTTOM BAR ---
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(15, 20, 15, 20));
        bottomBar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        
        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        backBtn.setPrefHeight(35);
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));
        
        bottomBar.getChildren().add(backBtn);
        root.setBottom(bottomBar);

        // ============================================
        // LOGIC & ACTIONS
        // ============================================
        
        Runnable refreshAll = () -> {
            // Load Debtors
            debtTable.setItems(FXCollections.observableArrayList(FinanceService.getLateReservations()));
            // Load History
            historyTable.setItems(FXCollections.observableArrayList(FinanceService.getAllPayments()));
            
            // Reset Form
            amountField.clear(); amountField.setDisable(true);
            payBtn.setDisable(true);
            infoLabel.setText("Select a reservation from the list...");
            infoLabel.setStyle("-fx-text-fill: #7f8c8d;");
            statusMsg.setText("");
            selectedResId = -1;
        };
        refreshAll.run(); // Run on startup

        // Handle Selection in Debt Table
        debtTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedResId = newVal.getId();
                double remaining = FinanceService.calculateRemainingBill(selectedResId);
                
                infoLabel.setText("Client: " + newVal.getClientName() + "\n" +
                                  "Room: " + newVal.getRoomNumber() + "\n" +
                                  "Remaining Due: $" + String.format("%.2f", remaining));
                infoLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                
                amountField.setDisable(false);
                payBtn.setDisable(false);
                statusMsg.setText("");
            }
        });

        // Handle Pay Button
        payBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) { throw new NumberFormatException(); }

                if (FinanceService.processPayment(selectedResId, amount)) {
                    statusMsg.setText("✅ Payment Successful!");
                    statusMsg.setTextFill(Color.GREEN);
                    refreshAll.run(); 
                } else {
                    statusMsg.setText("❌ Payment Failed.");
                    statusMsg.setTextFill(Color.RED);
                }
            } catch (NumberFormatException ex) {
                statusMsg.setText("⚠️ Invalid Amount");
                statusMsg.setTextFill(Color.ORANGE);
            }
        });

        return new Scene(root, 900, 600);
    }
}
