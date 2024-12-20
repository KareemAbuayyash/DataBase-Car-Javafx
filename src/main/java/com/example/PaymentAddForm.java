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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentAddForm {
    private GridPane form;
    private DatePicker dpPaymentDate;
    private ComboBox<Integer> cbOrderID;
    private TextField txtPaymentMethod;
    private TextField txtAmount;
    private PaymentsSection paymentsSection;
    private Label messageLabel; // Message label for displaying validation errors

    public PaymentAddForm(PaymentsSection paymentsSection) {
        this.paymentsSection = paymentsSection;
        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        // Labels and Controls
        Label lblPaymentDate = new Label("Payment Date:");
        dpPaymentDate = new DatePicker();
        dpPaymentDate.setValue(LocalDate.now());

        Label lblOrderID = new Label("Order ID:");
        cbOrderID = new ComboBox<>();
        orderids();

        Label lblPaymentMethod = new Label("Payment Method:");
        txtPaymentMethod = new TextField();

        Label lblAmount = new Label("Amount:");
        txtAmount = new TextField();

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        addInputConstraints();

        form.add(lblPaymentDate, 0, 0);
        form.add(dpPaymentDate, 1, 0);
        form.add(lblOrderID, 0, 1);
        form.add(cbOrderID, 1, 1);
        form.add(lblPaymentMethod, 0, 2);
        form.add(txtPaymentMethod, 1, 2);
        form.add(lblAmount, 0, 3);
        form.add(txtAmount, 1, 3);
        form.add(messageLabel, 1, 4);

        Button btnSave = ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> savePayment());

        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> paymentsSection.hideAddForm());

        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

        form.add(hboxButtons, 1, 5);
    }

    private void addInputConstraints() {
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtAmount.setText(oldValue);
                messageLabel.setText("Amount must contain only numbers.");
            } else {
                messageLabel.setText("");
            }
        });
        txtPaymentMethod.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                txtPaymentMethod.setText(oldValue);
                messageLabel.setText("Payment Method must contain only letters .");
            } else {
                messageLabel.setText("");
            }
        });

        dpPaymentDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isAfter(LocalDate.now())) {
                dpPaymentDate.setValue(oldValue);
                messageLabel.setText("Payment Date must be today or in the past.");
            } else {
                messageLabel.setText("");
            }
        });
    }

    private void orderids() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            String q2 = "SELECT OrderID FROM Orders ORDER BY OrderID ASC";
            ResultSet resultSet = stmt.executeQuery(q2);
            List<Integer> orderIDs = new ArrayList<>();
            while (resultSet.next()) {
                orderIDs.add(resultSet.getInt("OrderID"));
            }
            ObservableList<Integer> observableList = FXCollections.observableArrayList(orderIDs);
            cbOrderID.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePayment() {
        LocalDate paymentDateValue = dpPaymentDate.getValue();
        Integer orderID = cbOrderID.getValue();
        String paymentMethod = txtPaymentMethod.getText();
        String amountStr = txtAmount.getText();

        if (paymentDateValue == null || orderID == null || paymentMethod.isEmpty() || amountStr.isEmpty()) {
            messageLabel.setText("All fields are required and must be valid.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            messageLabel.setText("Amount must be a valid number.");
            return;
        }

        if (amount <= 0) {
            messageLabel.setText("Amount must be positive.");
            return;
        }

        try {
            Date paymentDate = Date.valueOf(paymentDateValue);
            String sql = "INSERT INTO Payments (PaymentDate, OrderID, PaymentMethod, Amount) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, paymentDate);
                pstmt.setInt(2, orderID);
                pstmt.setString(3, paymentMethod);
                pstmt.setDouble(4, amount);

                pstmt.executeUpdate();
                messageLabel.setText("Payment saved successfully.");
                messageLabel.setStyle("-fx-text-fill: green;");
                clearFields();
                paymentsSection.reloadScene();
            } catch (SQLException e) {
                e.printStackTrace();
                messageLabel.setText("Error saving payment: " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            messageLabel.setText("An error occurred: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearFields() {
        dpPaymentDate.setValue(LocalDate.now());
        cbOrderID.getSelectionModel().clearSelection();
        txtPaymentMethod.clear();
        txtAmount.clear();
        messageLabel.setText("");
    }

    public GridPane getForm() {
        return form;
    }

    public void showAddForm() {
        clearFields();
    }
}
