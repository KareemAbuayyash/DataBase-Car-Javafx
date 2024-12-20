package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerAddForm {
    private GridPane form;
    private TextField txtFirstName;
    private TextField txtLastName;
    private TextField txtEmail;
    private TextField txtPhone;
    private TextField txtAddress;
    private TextField txtCity;
    private TextField txtState;
    private TextField txtZipCode;
    private Label messageLabel; // For displaying validation messages
    private CustomerSection customerSection;

    public CustomerAddForm(CustomerSection customerSection) {
        this.customerSection = customerSection;

        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(15); // Increased vertical gap
        form.setHgap(10);

        // Labels and TextFields
        Label lblFirstName = new Label("First Name:");
        lblFirstName.setPadding(new Insets(2, 0, 2, 0));
        txtFirstName = new TextField();
        txtFirstName.setPadding(new Insets(2, 2, 2, 2));
        txtFirstName.setMinWidth(200);

        Label lblLastName = new Label("Last Name:");
        lblLastName.setPadding(new Insets(2, 0, 2, 0));
        txtLastName = new TextField();
        txtLastName.setPadding(new Insets(2, 2, 2, 2));
        txtLastName.setMinWidth(200);

        Label lblEmail = new Label("Email:");
        lblEmail.setPadding(new Insets(2, 0, 2, 0));
        txtEmail = new TextField();
        txtEmail.setPadding(new Insets(2, 2, 2, 2));
        txtEmail.setMinWidth(200);

        Label lblPhone = new Label("Phone:");
        lblPhone.setPadding(new Insets(2, 0, 2, 0));
        txtPhone = new TextField();
        txtPhone.setPadding(new Insets(2, 2, 2, 2));
        txtPhone.setMinWidth(200);

        Label lblAddress = new Label("Address:");
        lblAddress.setPadding(new Insets(2, 0, 2, 0));
        txtAddress = new TextField();
        txtAddress.setPadding(new Insets(2, 2, 2, 2));
        txtAddress.setMinWidth(200);

        Label lblCity = new Label("City:");
        lblCity.setPadding(new Insets(2, 0, 2, 0));
        txtCity = new TextField();
        txtCity.setPadding(new Insets(2, 2, 2, 2));
        txtCity.setMinWidth(200);

        Label lblState = new Label("State:");
        lblState.setPadding(new Insets(2, 0, 2, 0));
        txtState = new TextField();
        txtState.setPadding(new Insets(2, 2, 2, 2));
        txtState.setMinWidth(200);

        Label lblZipCode = new Label("Zip Code:");
        lblZipCode.setPadding(new Insets(2, 0, 2, 0));
        txtZipCode = new TextField();
        txtZipCode.setPadding(new Insets(2, 2, 2, 2));
        txtZipCode.setMinWidth(200);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        addInputConstraints();

        // Layout
        form.add(lblFirstName, 0, 0);
        form.add(txtFirstName, 1, 0);
        form.add(lblLastName, 2, 0);
        form.add(txtLastName, 3, 0);
        form.add(lblEmail, 0, 1);
        form.add(txtEmail, 1, 1);
        form.add(lblPhone, 2, 1);
        form.add(txtPhone, 3, 1);
        form.add(lblAddress, 0, 2);
        form.add(txtAddress, 1, 2);
        form.add(lblCity, 2, 2);
        form.add(txtCity, 3, 2);
        form.add(lblState, 0, 3);
        form.add(txtState, 1, 3);
        form.add(lblZipCode, 2, 3);
        form.add(txtZipCode, 3, 3);
        form.add(messageLabel, 1, 4, 3, 1); // Span the message label across multiple columns for better visibility

        // Buttons
        Button btnSave =  ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> saveCustomer());
        Button btnCancel =  ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> customerSection.hideAddForm());

        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);
        form.add(hboxButtons, 0, 6, 4, 1); // Span the buttons across multiple columns

    }

    private void addInputConstraints() {
        // First Name: Letters only
        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9]*")) {
                txtFirstName.setText(oldValue);
                messageLabel.setText("First Name must contain only letter and number.");
            } else {
                messageLabel.setText("");
            }
        });

        // Last Name: Letters only
        txtLastName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9]*")) {
                txtLastName.setText(oldValue);
                messageLabel.setText("Last Name must contain only letters and number.");
            } else {
                messageLabel.setText("");
            }
        });

        // Email: Simple email pattern validation
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                messageLabel.setText("Enter a valid email address ex: example@example.com");
            } else {
                messageLabel.setText("");
            }
        });

        // Phone: Must be in the format 123-4567
        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,3}(-\\d{0,5})?")) {
                txtPhone.setText(oldValue);
                messageLabel.setText("Phone must be in the format 123-4567.");
            } else if (newValue.length() > 8) {
                txtPhone.setText(oldValue);
                messageLabel.setText("Phone must be exactly 7 digits.");
            } else {
                messageLabel.setText("");
            }
        });

        // Address: Allow letters, digits, spaces, and commas
        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9 ,]*")) {
                txtAddress.setText(oldValue);
                messageLabel.setText("Address contains ex: 123 Main St.");
            } else {
                messageLabel.setText("");
            }
        });

        // City: Letters only
        txtCity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9 ]*")) {
                txtCity.setText(oldValue);
                messageLabel.setText("City must contain only letters and number.");
            } else {
                messageLabel.setText("");
            }
        });

        // State: Letters only
        txtState.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9 ]*")) {
                txtState.setText(oldValue);
                messageLabel.setText("State must contain only letters and number.");
            } else {
                messageLabel.setText("");
            }
        });

        // Zip Code: Digits only
        txtZipCode.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || newValue.length() > 5) {
                txtZipCode.setText(oldValue);
                messageLabel.setText("Zip Code must be exactly 5 digits.");
            } else {
                messageLabel.setText("");
            }
        });
    }

    private void saveCustomer() {
        messageLabel.setText(""); // Clear previous messages

        // Get input values
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String address = txtAddress.getText();
        String city = txtCity.getText();
        String state = txtState.getText();
        String zipCode = txtZipCode.getText();

        // Input validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || city.isEmpty() || state.isEmpty() || zipCode.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }
        if (zipCode.length() != 5) {
            messageLabel.setText("VIN must be exactly 5 digits.");
            return;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            messageLabel.setText("Enter a valid email address ex: example@example.com");
            return;
        }
        if (!phone.matches("\\d{3}-\\d{4}")) {
            messageLabel.setText("Phone number must be in the format 123-4567.");
            return;
        }

        String sql = "INSERT INTO customers (FirstName, LastName, Email, Phone, Address, City, State, ZipCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.setString(6, city);
            pstmt.setString(7, state);
            pstmt.setString(8, zipCode);

            pstmt.executeUpdate();

            // Success Message
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Customer added successfully!");

            // Clear fields
            clearFields();

            // Refresh data
            customerSection.reloadScene();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Database Error: Unable to save customer.");
        }
    }

    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtCity.clear();
        txtState.clear();
        txtZipCode.clear();
    }

    public GridPane getForm() {
        return form;
    }

    public void showAddForm() {
        clearFields();
        messageLabel.setText("");
    }
}
