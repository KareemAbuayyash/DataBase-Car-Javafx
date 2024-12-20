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

public class ServiceAddForm {
    private GridPane form;
    private ComboBox<Integer> cbCarID;
    private ComboBox<Integer> cbCustomerID;
    private DatePicker dpServiceDate;
    private TextField txtServiceDescription;
    private TextField txtCost;
    private ServicesSection servicesSection;
    private Label messageLabel; // Label for displaying validation messages

    public ServiceAddForm(ServicesSection servicesSection) {
        this.servicesSection = servicesSection;
        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        // Initialize controls
        cbCarID = new ComboBox<>();
        cbCustomerID = new ComboBox<>();
        loadCarIDs();  // Load car IDs into ComboBox
        loadCustomerIDs();  // Load customer IDs into ComboBox

        dpServiceDate = new DatePicker(LocalDate.now());
        txtServiceDescription = new TextField();
        txtCost = new TextField();

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        addInputConstraints();

        // Layout setup
        form.add(new Label("Car ID:"), 0, 0);
        form.add(cbCarID, 1, 0);
        form.add(new Label("Customer ID:"), 0, 1);
        form.add(cbCustomerID, 1, 1);
        form.add(new Label("Service Date:"), 0, 2);
        form.add(dpServiceDate, 1, 2);
        form.add(new Label("Service Description:"), 0, 3);
        form.add(txtServiceDescription, 1, 3);
        form.add(new Label("Cost:"), 0, 4);
        form.add(txtCost, 1, 4);
        form.add(messageLabel, 1, 5);

        // Buttons
        Button btnSave = ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> saveService());
        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> servicesSection.hideAddForm());
        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);
        form.add(hboxButtons, 1, 6);
    }

    private void addInputConstraints() {
        txtCost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtCost.setText(oldValue);
                messageLabel.setText("Cost must be a valid number.");
            } else {
                messageLabel.setText("");
            }
        });

        // text description  and should not be empty or null and should not contain only  spaces and letter
        txtServiceDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                txtServiceDescription.setText(oldValue);
                messageLabel.setText("Description must contain only letters .");
            } else {
                messageLabel.setText("");
            }
        });
    }

    private void loadCarIDs() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CarID FROM Cars ORDER BY CarID")) {
            List<Integer> carIDs = new ArrayList<>();
            while (rs.next()) {
                carIDs.add(rs.getInt("CarID"));
            }
            cbCarID.setItems(FXCollections.observableArrayList(carIDs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerIDs() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CustomerID FROM Customers ORDER BY CustomerID")) {
            List<Integer> customerIDs = new ArrayList<>();
            while (rs.next()) {
                customerIDs.add(rs.getInt("CustomerID"));
            }
            cbCustomerID.setItems(FXCollections.observableArrayList(customerIDs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveService() {
        if (cbCarID.getValue() == null || cbCustomerID.getValue() == null ||
                dpServiceDate.getValue() == null || txtServiceDescription.getText().isEmpty() ||
                txtCost.getText().isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        double cost;
        try {
            cost = Double.parseDouble(txtCost.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Cost must be a valid number.");
            return;
        }

        if (cost <= 0) {
            messageLabel.setText("Cost must be positive.");
            return;
        }

        try {
            String sql = "INSERT INTO Services (CarID, CustomerID, ServiceDate, ServiceDescription, Cost) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, cbCarID.getValue());
                pstmt.setInt(2, cbCustomerID.getValue());
                pstmt.setDate(3, Date.valueOf(dpServiceDate.getValue()));
                pstmt.setString(4, txtServiceDescription.getText());
                pstmt.setDouble(5, cost);

                pstmt.executeUpdate();
                messageLabel.setText("Service saved successfully.");
                messageLabel.setStyle("-fx-text-fill: green;");
                clearFields();
                servicesSection.reloadScene();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Error saving service: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void clearFields() {
        cbCarID.getSelectionModel().clearSelection();
        cbCustomerID.getSelectionModel().clearSelection();
        dpServiceDate.setValue(LocalDate.now());
        txtServiceDescription.clear();
        txtCost.clear();
        messageLabel.setText("");
    }

    public GridPane getForm() {
        return form;
    }

    public void showAddForm() {
        clearFields();
    }
}
