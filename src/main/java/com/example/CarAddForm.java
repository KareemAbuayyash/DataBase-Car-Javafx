package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CarAddForm {
    private GridPane form;
    private TextField txtMake;
    private TextField txtModel;
    private TextField txtYear;
    private TextField txtPrice;
    private TextField txtStock;
    private TextField txtVIN;
    private Label messageLabel; // For displaying messages
    private CarSection carSection;

    public CarAddForm(CarSection carSection) {
        this.carSection = carSection;

        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        // Labels and TextFields
        Label lblMake = new Label("Make:");
        txtMake = new TextField();
        Label lblModel = new Label("Model:");
        txtModel = new TextField();
        Label lblYear = new Label("Year:");
        txtYear = new TextField();
        Label lblPrice = new Label("Price:");
        txtPrice = new TextField();
        Label lblStock = new Label("Stock:");
        txtStock = new TextField();
        Label lblVIN = new Label("VIN:");
        txtVIN = new TextField();

        addInputConstraints();

        form.add(lblMake, 0, 0);
        form.add(txtMake, 1, 0);
        form.add(lblModel, 0, 1);
        form.add(txtModel, 1, 1);
        form.add(lblYear, 0, 2);
        form.add(txtYear, 1, 2);
        form.add(lblPrice, 0, 3);
        form.add(txtPrice, 1, 3);
        form.add(lblStock, 0, 4);
        form.add(txtStock, 1, 4);
        form.add(lblVIN, 0, 5);
        form.add(txtVIN, 1, 5);

        Button btnSave = ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> saveCar());

        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> carSection.hideAddForm());

        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);
        form.add(hboxButtons, 1, 6);

        // Message label for feedback
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        form.add(messageLabel, 1, 7);
    }

    private void addInputConstraints() {
        // Allow alphanumeric characters for Make
        txtMake.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtMake.setText(oldValue);
                messageLabel.setText("Make field must contain only letters .");
            } else {
                messageLabel.setText("");
            }
        });
    
        // Allow alphanumeric characters for Model
        txtModel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9 ]*")) {
                txtModel.setText(oldValue);
                messageLabel.setText("Model field must contain only letters and numbers.");
            } else {
                messageLabel.setText("");
            }
        });
    
        // Allow only digits for Year and Stock
        txtYear.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtYear.setText(oldValue);
                messageLabel.setText("Year must contain only numbers.");
            } else {
                messageLabel.setText("");
            }
        });
    
        txtStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtStock.setText(oldValue);
                messageLabel.setText("Stock must contain only numbers.");
            } else {
                messageLabel.setText("");
            }
        });
    
        // Allow only numbers and one decimal for Price
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtPrice.setText(oldValue);
                messageLabel.setText("Price must be a valid number.");
            } else {
                messageLabel.setText("");
            }
        });
    
        // VIN: Only numeric input, exactly 17 digits
        txtVIN.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtVIN.setText(oldValue);
                messageLabel.setText("VIN must contain only numbers.");
            } else if (newValue.length() > 17) {
                txtVIN.setText(oldValue);
                messageLabel.setText("VIN must be exactly 17 digits.");
            } else {
                messageLabel.setText("");
            }
        });
    }
    

   private void saveCar() {
    messageLabel.setText(""); // Clear previous messages

    String make = txtMake.getText();
    String model = txtModel.getText();
    String yearStr = txtYear.getText();
    String priceStr = txtPrice.getText();
    String stockStr = txtStock.getText();
    String vin = txtVIN.getText();

    // Input validation
    if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || vin.isEmpty()) {
        messageLabel.setText("All fields are required.");
        return;
    }

    if (vin.length() != 17) {
        messageLabel.setText("VIN must be exactly 17 digits.");
        return;
    }

    int year;
    double price;
    int stock;

    try {
        year = Integer.parseInt(yearStr);
        price = Double.parseDouble(priceStr);
        stock = Integer.parseInt(stockStr);
    } catch (NumberFormatException e) {
        messageLabel.setText("Year, Price, and Stock must be valid numbers.");
        return;
    }

    String sql = "INSERT INTO Cars (Make, Model, Year, Price, Stock, VIN) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, make);
        pstmt.setString(2, model);
        pstmt.setInt(3, year);
        pstmt.setDouble(4, price);
        pstmt.setInt(5, stock);
        pstmt.setString(6, vin);

        pstmt.executeUpdate();

        // Success Message
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
        messageLabel.setText("Car added successfully!");

        // Optionally clear fields
        clearFields();

        // Refresh scene
        carSection.reloadScene();
    } catch (SQLException e) {
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        messageLabel.setText("Error: VIN must be unique and exactly 17 digits.");
    }
}

private void clearFields() {
    txtMake.clear();
    txtModel.clear();
    txtYear.clear();
    txtPrice.clear();
    txtStock.clear();
    txtVIN.clear();
}


    public GridPane getForm() {
        return form;
    }

    public void showAddForm() {
        clearFields(); 
        messageLabel.setText("");
    }
}
