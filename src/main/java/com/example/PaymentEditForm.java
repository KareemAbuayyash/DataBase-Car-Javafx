package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class PaymentEditForm {
    private GridPane form;
    private DatePicker dpPaymentDate;
    private TextField txtOrderID;
    private TextField txtPaymentMethod;
    private TextField txtAmount;
    private Payment currentPayment;
    private PaymentsSection paymentsSection;
    private Label messageLabel; // Message label for displaying validation errors

    public PaymentEditForm(PaymentsSection paymentsSection) {
        this.paymentsSection = paymentsSection;
        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        // Labels and Controls
        Label lblOrderID = new Label("Order ID:");
        txtOrderID = new TextField();
        txtOrderID.setEditable(false); // Order ID should not be editable

        Label lblPaymentDate = new Label("Payment Date:");
        dpPaymentDate = new DatePicker();

        Label lblPaymentMethod = new Label("Payment Method:");
        txtPaymentMethod = new TextField();

        Label lblAmount = new Label("Amount:");
        txtAmount = new TextField();

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        addInputConstraints();

        form.add(lblOrderID, 0, 0);
        form.add(txtOrderID, 1, 0);
        form.add(lblPaymentDate, 0, 1);
        form.add(dpPaymentDate, 1, 1);
        form.add(lblPaymentMethod, 0, 2);
        form.add(txtPaymentMethod, 1, 2);
        form.add(lblAmount, 0, 3);
        form.add(txtAmount, 1, 3);
        form.add(messageLabel, 1, 4);

        Button btnSave = ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> savePayment());

        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> paymentsSection.hideEditForm());

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
                messageLabel.setText("Payment Method must contain only letter.");
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

    public void showEditForm(Payment payment) {
        this.currentPayment = payment;
        txtOrderID.setText(String.valueOf(payment.getOrderID()));
        dpPaymentDate.setValue(payment.getPaymentDate());
        txtPaymentMethod.setText(payment.getPaymentMethod());
        txtAmount.setText(String.format("%.2f", payment.getAmount()));
    }

    private void savePayment() {
        try {
            int orderID = Integer.parseInt(txtOrderID.getText());
            LocalDate paymentDateValue = dpPaymentDate.getValue();
            String paymentMethod = txtPaymentMethod.getText();
            double amount = Double.parseDouble(txtAmount.getText());

            if (paymentDateValue == null || paymentMethod.isEmpty() || amount <= 0) {
                messageLabel.setText("All fields are required and Amount must be positive.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String sql = "UPDATE payments SET PaymentDate = ?, PaymentMethod = ?, Amount = ? WHERE OrderID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, Date.valueOf(paymentDateValue));
                pstmt.setString(2, paymentMethod);
                pstmt.setDouble(3, amount);
                pstmt.setInt(4, orderID);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    messageLabel.setText("Payment updated successfully.");
                    messageLabel.setStyle("-fx-text-fill: green;");
                    clearFields();

                    paymentsSection.reloadScene();

                } else {
                    messageLabel.setText("No changes were made to the record.");
                    messageLabel.setStyle("-fx-text-fill: orange;");
                }
            } catch (SQLException e) {
                messageLabel.setText("Database error: " + e.getMessage());
                messageLabel.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number format in Amount.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public GridPane getForm() {
        return form;
    }
    private void clearFields() {
        dpPaymentDate.setValue(LocalDate.now());
        txtOrderID.clear();
        txtPaymentMethod.clear();
        txtAmount.clear();
        messageLabel.setText("");
    }
}
