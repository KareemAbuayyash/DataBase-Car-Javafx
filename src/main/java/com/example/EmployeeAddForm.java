package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeAddForm {
    private GridPane form;
    private TextField txtFirstName;
    private TextField txtLastName;
    private TextField txtPosition;
    private TextField txtSalary;
    private DatePicker txtHireDate;
    private Label messageLabel;
    private EmployeeSection employeeSection;

    public EmployeeAddForm(EmployeeSection employeeSection) {
        this.employeeSection = employeeSection;
        form = new GridPane();
        form.setPadding(new Insets(20));
        form.setVgap(10);
        form.setHgap(10);

        // Labels and TextFields
        Label lblFirstName = new Label("First Name:");
        txtFirstName = new TextField();
        Label lblLastName = new Label("Last Name:");
        txtLastName = new TextField();
        Label lblPosition = new Label("Position:");
        txtPosition = new TextField();
        Label lblSalary = new Label("Salary:");
        txtSalary = new TextField();
        Label lblHireDate = new Label("Hire Date:");
        txtHireDate =  new DatePicker();

        form.add(lblFirstName, 0, 0);
        form.add(txtFirstName, 1, 0);
        form.add(lblLastName, 0, 1);
        form.add(txtLastName, 1, 1);
        form.add(lblPosition, 0, 2);
        form.add(txtPosition, 1, 2);
        form.add(lblSalary, 0, 3);
        form.add(txtSalary, 1, 3);
        form.add(lblHireDate, 0, 4);
        form.add(txtHireDate, 1, 4);

        Button btnSave =  ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> saveEmployee());

        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> employeeSection.hideAddForm());

        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

        form.add(hboxButtons, 1, 5);
        // Message Label for Feedback
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        form.add(messageLabel, 1, 6);
        addInputConstraints();
    }

    private void addInputConstraints() {
        // First Name: Letters only
        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtFirstName.setText(oldValue);
                messageLabel.setText("First Name must contain only letter.");
            } else {
                messageLabel.setText("");
            }
        });

        // Last Name: Letters only
        txtLastName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtLastName.setText(oldValue);
                messageLabel.setText("Last Name must contain only letters.");
            } else {
                messageLabel.setText("");
            }
        });

        txtPosition.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtPosition.setText(oldValue);
                messageLabel.setText("Position must contain only letters.");
            } else {
                messageLabel.setText("");
            }
        });
        txtSalary.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtSalary.setText(oldValue);
                messageLabel.setText("Salary must contain only numbers.");
            } else {
                messageLabel.setText("");
            }
        });
       txtHireDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                messageLabel.setText("Hire Date is required.");
            } else {
                messageLabel.setText("");
            }
        });
    }

    public GridPane getForm() {
        return form;
    }

    public void showAddForm() {
        clearFields(); 
        messageLabel.setText("");
    }

    private void saveEmployee() {
        // Get input values
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String position = txtPosition.getText();
        String salaryStr = txtSalary.getText();
        String hireDate = txtHireDate.getPromptText();

        // Input validation
        if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty() || salaryStr.isEmpty()
                || hireDate.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
        } catch (NumberFormatException e) {
            messageLabel.setText("Salary must be a valid number.");
            return;
        }

        String sql = "INSERT INTO employees (FirstName, LastName, Position, Salary, HireDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, position);
            pstmt.setDouble(4, salary);
            pstmt.setString(5, hireDate);

            pstmt.executeUpdate();
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            messageLabel.setText("Car added successfully!");

            // Optionally clear fields
            clearFields();

            // Refresh scene
            employeeSection.reloadScene();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            messageLabel.setText("Error: Unable to save employee.");
        }    
    }
    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        txtPosition.clear();
        txtSalary.clear();
        txtHireDate.setValue(null);
    }
}
