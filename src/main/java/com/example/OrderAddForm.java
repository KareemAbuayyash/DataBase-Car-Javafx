package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderAddForm {
    private GridPane form;
    private DatePicker dpOrderDate;
    private ComboBox<Integer> cbCarID;
    private ComboBox<Integer> cbCustomerID;
    private ComboBox<Integer> cbEmployeeID;
    private TextField txtQuantity;
    private TextField txtTotalPrice;
    private OrdersSection ordersSection;
    private Label messageLabel; // Message label for displaying validation errors

    public OrderAddForm(OrdersSection ordersSection) {
        this.ordersSection = ordersSection;
        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        // Labels and Controls
        Label lblOrderDate = new Label("Order Date:");
        dpOrderDate = new DatePicker();

        Label lblCarID = new Label("Car ID:");
        cbCarID = new ComboBox<>();
        carids();

        Label lblCustomerID = new Label("Customer ID:");
        cbCustomerID = new ComboBox<>();
        customerid();

        Label lblEmployeeID = new Label("Employee ID:");
        cbEmployeeID = new ComboBox<>();
        employeeid();

        Label lblQuantity = new Label("Quantity:");
        txtQuantity = new TextField();

        Label lblTotalPrice = new Label("Total Price:");
        txtTotalPrice = new TextField();

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        addInputConstraints();

        form.add(lblOrderDate, 0, 0);
        form.add(dpOrderDate, 1, 0);
        form.add(lblCarID, 0, 1);
        form.add(cbCarID, 1, 1);
        form.add(lblCustomerID, 0, 2);
        form.add(cbCustomerID, 1, 2);
        form.add(lblEmployeeID, 0, 3);
        form.add(cbEmployeeID, 1, 3);
        form.add(lblQuantity, 0, 4);
        form.add(txtQuantity, 1, 4);
        form.add(lblTotalPrice, 0, 5);
        form.add(txtTotalPrice, 1, 5);
        form.add(messageLabel, 1, 6);

        Button btnSave = ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> saveOrder());

        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> ordersSection.hideAddForm());

        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

        form.add(hboxButtons, 1, 7);
    }

    private void addInputConstraints() {
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQuantity.setText(oldValue);
                messageLabel.setText("Quantity must contain only numbers.");
            } else {
                messageLabel.setText("");
            }
        });

        txtTotalPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtTotalPrice.setText(oldValue);
                messageLabel.setText("Total Price must contain only numbers.");
            } else {
                messageLabel.setText("");
            }
        });

        dpOrderDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isAfter(java.time.LocalDate.now())) {
                dpOrderDate.setValue(oldValue);
                messageLabel.setText("Order Date must be today or in the past.");
            } else {
                messageLabel.setText("");
            }
        }); 
    }

    private void carids() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            String q2 = "SELECT CarID FROM Cars ORDER BY CarID ASC";
            ResultSet resultSet = stmt.executeQuery(q2);
            List<Integer> carIDs = new ArrayList<>();
            while (resultSet.next()) {
                carIDs.add(resultSet.getInt("CarID"));
            }
            ObservableList<Integer> observableList = FXCollections.observableArrayList(carIDs);
            cbCarID.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void customerid() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            String q2 = "SELECT CustomerID FROM Customers ORDER BY CustomerID ASC";
            ResultSet resultSet = stmt.executeQuery(q2);
            List<Integer> customerIDs = new ArrayList<>();
            while (resultSet.next()) {
                customerIDs.add(resultSet.getInt("CustomerID"));
            }
            ObservableList<Integer> observableList = FXCollections.observableArrayList(customerIDs);
            cbCustomerID.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void employeeid() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            String q2 = "SELECT EmployeeID FROM Employees ORDER BY EmployeeID ASC";
            ResultSet resultSet = stmt.executeQuery(q2);
            List<Integer> employeeIDs = new ArrayList<>();
            while (resultSet.next()) {
                employeeIDs.add(resultSet.getInt("EmployeeID"));
            }
            ObservableList<Integer> observableList = FXCollections.observableArrayList(employeeIDs);
            cbEmployeeID.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOrder() {
        java.time.LocalDate orderDateValue = dpOrderDate.getValue();
        Integer carID = cbCarID.getValue();
        Integer customerID = cbCustomerID.getValue();
        Integer employeeID = cbEmployeeID.getValue();
        String quantityStr = txtQuantity.getText();
        String totalPriceStr = txtTotalPrice.getText();

        if (orderDateValue == null || carID == null || customerID == null || employeeID == null || quantityStr.isEmpty() || totalPriceStr.isEmpty()) {
            messageLabel.setText("All fields are required and must be valid.");
            return;
        }

        int quantity;
        double totalPrice;
        try {
            quantity = Integer.parseInt(quantityStr);
            totalPrice = Double.parseDouble(totalPriceStr);
        } catch (NumberFormatException e) {
            messageLabel.setText("Quantity and Total Price must be valid numbers.");
            return;
        }

        if (quantity <= 0 || totalPrice <= 0) {
            messageLabel.setText("Quantity and Total Price must be positive values.");
            return;
        }

        try {
            Date orderDate = Date.valueOf(orderDateValue);
            String sql = "INSERT INTO orders (OrderDate, CarID, CustomerID, EmployeeID, Quantity, TotalPrice) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, orderDate);
                pstmt.setInt(2, carID);
                pstmt.setInt(3, customerID);
                pstmt.setInt(4, employeeID);
                pstmt.setInt(5, quantity);
                pstmt.setDouble(6, totalPrice);

                pstmt.executeUpdate();
                
                messageLabel.setText("Order saved successfully.");
                messageLabel.setStyle("-fx-text-fill: green;");
                clearFields();
                ordersSection.reloadScene();
            } catch (SQLException e) {
                e.printStackTrace();
                messageLabel.setText("Error saving order: " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            messageLabel.setText("An error occurred: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }
    private void clearFields() {
        dpOrderDate.setValue(null);
        cbCarID.getSelectionModel().clearSelection();
        cbCustomerID.getSelectionModel().clearSelection();
        cbEmployeeID.getSelectionModel().clearSelection();
        txtQuantity.clear();
        txtTotalPrice.clear();
    }
    public GridPane getForm() {
        return form;
    }

    public void showAddForm() {
        dpOrderDate.setValue(null);
        cbCarID.getSelectionModel().clearSelection();
        cbCustomerID.getSelectionModel().clearSelection();
        cbEmployeeID.getSelectionModel().clearSelection();
        txtQuantity.clear();
        txtTotalPrice.clear();
        messageLabel.setText("");
    }
}
