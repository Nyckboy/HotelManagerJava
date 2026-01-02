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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;

public class FinanceScreen {

    private static int selectedResId = -1;

    public static Scene getScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // TITLE
        Label mainTitle = new Label("FINANCE & BILLING");
        mainTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ============================================
        // TAB 1: PENDING BILLS (DEBTORS)
        // ============================================
        
        // 1. The Table
        TableView<Reservation> debtTable = new TableView<>();
        
        TableColumn<Reservation, Integer> colResId = new TableColumn<>("Res ID");
        colResId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Reservation, String> colClientName = new TableColumn<>("Client Name"); // <--- NEW NAME COLUMN
        colClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        TableColumn<Reservation, Integer> colRoom = new TableColumn<>("Room");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        debtTable.getColumns().addAll(colResId, colClientName, colRoom);
        debtTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 2. The Form (Right Side of Tab 1)
        VBox payForm = new VBox(10);
        payForm.setPrefWidth(250);
        payForm.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        payForm.setAlignment(Pos.CENTER);
        
        Label infoLabel = new Label("Select a reservation...");
        TextField amountField = new TextField(); amountField.setPromptText("Amount"); amountField.setDisable(true);
        Button payBtn = new Button("Pay Now"); payBtn.setDisable(true);
        Label statusMsg = new Label();

        payForm.getChildren().addAll(new Label("Process Payment"), infoLabel, amountField, payBtn, statusMsg);

        HBox tab1Content = new HBox(10, debtTable, payForm);
        HBox.setHgrow(debtTable, Priority.ALWAYS);
        
        Tab tab1 = new Tab("Pending Bills", tab1Content);
        tab1.setClosable(false);

        // ============================================
        // TAB 2: PAYMENT HISTORY (PAID LIST)
        // ============================================
        TableView<Paiement> historyTable = new TableView<>();
        
        TableColumn<Paiement, Integer> colPayId = new TableColumn<>("ID");
        colPayId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Paiement, Double> colAmount = new TableColumn<>("Amount Paid");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Paiement, LocalDate> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Paiement, Integer> colResRef = new TableColumn<>("Res Ref#");
        colResRef.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        historyTable.getColumns().addAll(colPayId, colAmount, colDate, colResRef);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Tab tab2 = new Tab("Payment History", historyTable);
        tab2.setClosable(false);

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
            infoLabel.setText("Select a reservation...");
            selectedResId = -1;
        };
        refreshAll.run(); // Run on startup

        // Handle Selection in Debt Table
        debtTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedResId = newVal.getId();
                double remaining = FinanceService.calculateRemainingBill(selectedResId);
                infoLabel.setText("Client: " + newVal.getClientName() + "\nDue: $" + remaining);
                amountField.setDisable(false);
                payBtn.setDisable(false);
            }
        });

        // Handle Pay Button
        payBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (FinanceService.processPayment(selectedResId, amount)) {
                    statusMsg.setText("Paid!");
                    statusMsg.setTextFill(Color.GREEN);
                    refreshAll.run(); // Refresh both tables
                }
            } catch (Exception ex) {
                statusMsg.setText("Invalid Amount");
            }
        });

        // ============================================
        // FINAL ASSEMBLY
        // ============================================
        TabPane tabPane = new TabPane(tab1, tab2);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> ScreenManager.setScene(DashboardScreen.getScene()));

        root.getChildren().addAll(mainTitle, tabPane, backBtn);
        return new Scene(root, 800, 550);
    }
}