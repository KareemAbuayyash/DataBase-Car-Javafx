package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.*;

public class ReportBView {
    private VBox view;
    private TableView<Order> tableView;
    private TextField txtEmployeeID;
    private Button btnGenerate;
    private Label lblErrorMessage; // Label for error messages

    public ReportBView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Input fields
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        txtEmployeeID = new TextField();
        txtEmployeeID.setPromptText("Employee ID");

        btnGenerate = ButtonFactory.createActionButton("Generate Report");
        btnGenerate.setOnAction(e -> generateReport());

        lblErrorMessage = new Label(); // Initialize the error message label
        lblErrorMessage.setStyle("-fx-text-fill: red;"); // Set red color for error text

        inputBox.getChildren().addAll(new Label("Employee ID:"), txtEmployeeID, btnGenerate);

        // TableView setup
        tableView = new TableView<>();
        tableView.setPrefHeight(400);

        TableColumn<Order, Integer> colOrderID = new TableColumn<>("Order ID");
        colOrderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));

        TableColumn<Order, Date> colOrderDate = new TableColumn<>("Order Date");
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<Order, Integer> colCarID = new TableColumn<>("Car ID");
        colCarID.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<Order, Integer> colCustomerID = new TableColumn<>("Customer ID");
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<Order, Integer> colEmployeeID = new TableColumn<>("Employee ID");
        colEmployeeID.setCellValueFactory(new PropertyValueFactory<>("employeeID"));

        TableColumn<Order, Integer> colQuantity = new TableColumn<>("Quantity");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, Double> colTotalPrice = new TableColumn<>("Total Price");
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        tableView.getColumns().addAll(colOrderID, colOrderDate, colCarID, colCustomerID, colEmployeeID, colQuantity, colTotalPrice);
        tableView.getColumns().forEach(column -> column.setReorderable(false));

        // Add input box, error label, and table to the view
        view.getChildren().addAll(inputBox, lblErrorMessage, tableView);
    }

    public VBox getView() {
        return view;
    }

    private void generateReport() {
        lblErrorMessage.setText(""); // Clear the error message

        String employeeID = txtEmployeeID.getText().trim();

        if (employeeID.isEmpty()) {
            lblErrorMessage.setText("Please enter an Employee ID.");
            return;
        }

        String query = "SELECT * FROM orders WHERE EmployeeID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(employeeID));
            ResultSet rs = pstmt.executeQuery();

            ObservableList<Order> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Order(
                        rs.getInt("OrderID"),
                        rs.getDate("OrderDate"),
                        rs.getInt("CarID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalPrice")
                ));
            }
            tableView.setItems(data);

        } catch (SQLException e) {
            lblErrorMessage.setText("Database error occurred. Please try again.");
        } catch (NumberFormatException e) {
            lblErrorMessage.setText("Invalid input. Please ensure the Employee ID is number.");
        }
    }
}
