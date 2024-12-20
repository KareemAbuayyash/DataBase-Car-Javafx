package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportCView {
    private VBox view;
    private TableView<Payment> tableView;
    private TextField txtCustomerID;
    private Button btnGenerate;

    public ReportCView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Input fields
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        txtCustomerID = new TextField();
        txtCustomerID.setPromptText("Customer ID");

        btnGenerate = ButtonFactory.createActionButton("Generate Report");
        btnGenerate.setOnAction(e -> generateReport());

        inputBox.getChildren().addAll(new Label("Customer ID:"), txtCustomerID, btnGenerate);

        // TableView setup
        tableView = new TableView<>();
        tableView.setPrefHeight(400);

        TableColumn<Payment, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Payment, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Payment, String> colPaymentMethod = new TableColumn<>("Payment Method");
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<Payment, Double> colTotalAmount = new TableColumn<>("Total Amount");
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableView.getColumns().addAll(colFirstName, colLastName, colPaymentMethod, colTotalAmount);
        tableView.getColumns().forEach(column -> column.setReorderable(false));

        view.getChildren().addAll(inputBox, tableView);
    }

    public VBox getView() {
        return view;
    }

    private void generateReport() {
        String customerID = txtCustomerID.getText().trim();

        if (customerID.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Customer ID.");
            return;
        }

        String query = "SELECT c.FirstName, c.LastName, p.PaymentMethod, SUM(p.Amount) AS TotalAmount " +
                "FROM payments p " +
                "JOIN orders o ON p.OrderID = o.OrderID " +
                "JOIN customers c ON o.CustomerID = c.CustomerID " +
                "WHERE o.CustomerID = ? " +
                "GROUP BY c.FirstName, c.LastName, p.PaymentMethod";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(customerID));
            ResultSet rs = pstmt.executeQuery();

            ObservableList<Payment> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Payment(
                        0, // Placeholder PaymentID as it's not used here
                        0, // Placeholder OrderID as it's not used here
                        null, // PaymentDate not needed here
                        rs.getString("PaymentMethod"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("FirstName"),
                        rs.getString("LastName")));
            }
            tableView.setItems(data);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input or database error.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
