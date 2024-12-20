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

public class EmployeeEditForm {
    private GridPane form;
    private TextField txtFirstName;
    private TextField txtLastName;
    private TextField txtPosition;
    private TextField txtSalary;
    private DatePicker txtHireDate;
    private Employee currentEmployee;
    private EmployeeSection employeeSection;    
    private Label messageLabel; // Validation message display


    public EmployeeEditForm(EmployeeSection employeeSection) {
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
        txtHireDate = new DatePicker();
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        addInputConstraints();
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
        form.add(messageLabel, 1, 5);

        Button btnSave = ButtonFactory.createActionButton("Save");
        btnSave.setOnAction(e -> saveEmployee());

        Button btnCancel = ButtonFactory.createActionButton("Cancel");
        btnCancel.setOnAction(e -> employeeSection.hideEditForm());

        HBox hboxButtons = new HBox(10, btnSave, btnCancel);
        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

        form.add(hboxButtons, 1, 6);
       

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

        //make position letters only and spaces
        txtPosition.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                txtPosition.setText(oldValue);
                messageLabel.setText("Position must contain only letters and spaces.");
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

    public void showEditForm(Employee employee) {
        this.currentEmployee = employee;
        txtFirstName.setText(employee.getFirstName());
        txtLastName.setText(employee.getLastName());
        txtPosition.setText(employee.getPosition()); 
        txtSalary.setText(String.valueOf(employee.getSalary()));
        txtHireDate.setValue(employee.getHireDate().toLocalDate());
    }

    private void saveEmployee() {
        messageLabel.setText("");
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String position = txtPosition.getText();
        String salaryStr = txtSalary.getText();
        String hireDateStr = txtHireDate.getValue() != null ? txtHireDate.getValue().toString() : "";

        if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty() || salaryStr.isEmpty() || hireDateStr.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }
        currentEmployee.setFirstName(txtFirstName.getText());
        currentEmployee.setLastName(txtLastName.getText());
        currentEmployee.setPosition(txtPosition.getText());
        currentEmployee.setSalary(Double.parseDouble(txtSalary.getText()));
        currentEmployee.setHireDate(Date.valueOf(txtHireDate.getValue()));

        String sql = "UPDATE employees SET FirstName = ?, LastName = ?, Position = ?, Salary = ?, HireDate = ? WHERE EmployeeID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentEmployee.getFirstName());
            pstmt.setString(2, currentEmployee.getLastName());
            pstmt.setString(3, currentEmployee.getPosition());
            pstmt.setDouble(4, currentEmployee.getSalary());
            pstmt.setDate(5, currentEmployee.getHireDate());
            pstmt.setInt(6, currentEmployee.getEmployeeID());

            pstmt.executeUpdate();
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Car updated successfully!");
            employeeSection.reloadScene();
        } catch (SQLException e) {
            e.printStackTrace();
        }
}
}