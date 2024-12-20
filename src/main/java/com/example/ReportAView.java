package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;
import java.time.LocalDate;

public class ReportAView {
    private VBox view;
    private TableView<Service> tableView;
    private TextField txtCarID;
    private TextField txtCustomerID;
    private Button btnGenerate;
    private Label lblErrorMessage; // Label for error messages

    public ReportAView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Input fields
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        txtCarID = new TextField();
        txtCarID.setPromptText("Car ID");

        txtCustomerID = new TextField();
        txtCustomerID.setPromptText("Customer ID");

        btnGenerate = ButtonFactory.createActionButton("Generate Report");
        btnGenerate.setOnAction(e -> generateReport());

        lblErrorMessage = new Label(); // Initialize the error message label
        lblErrorMessage.setStyle("-fx-text-fill: red;"); // Set red color for error text

        inputBox.getChildren().addAll(new Label("Car ID:"), txtCarID, new Label("Customer ID:"), txtCustomerID,
                btnGenerate);

        // TableView setup
        tableView = new TableView<>();
        tableView.setPrefHeight(400);

        TableColumn<Service, Integer> colServiceID = new TableColumn<>("Service ID");
        colServiceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));

        TableColumn<Service, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Service, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Service, Integer> colCarID = new TableColumn<>("Car ID");
        colCarID.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<Service, Integer> colCustomerID = new TableColumn<>("Customer ID");
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<Service, String> colCarMake = new TableColumn<>("Car Make");
        colCarMake.setCellValueFactory(new PropertyValueFactory<>("carMake"));

        TableColumn<Service, LocalDate> colServiceDate = new TableColumn<>("Service Date");
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));

        TableColumn<Service, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));

        TableColumn<Service, Double> colCost = new TableColumn<>("Cost");
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableColumn<Service, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Service, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Add columns to TableView
        tableView.getColumns().addAll(colServiceID, colFirstName, colLastName, colCarID, colCustomerID, colCarMake,
                colServiceDate, colDescription, colCost, colEmail, colPhone);
        tableView.getColumns().forEach(column -> column.setReorderable(false));

        // Add input box, error label, and table to the view
        view.getChildren().addAll(inputBox, lblErrorMessage, tableView);
    }

    public VBox getView() {
        return view;
    }

    private void generateReport() {
        lblErrorMessage.setText(""); // Clear the error message

        String carID = txtCarID.getText().trim();
        String customerID = txtCustomerID.getText().trim();

        StringBuilder query = new StringBuilder(
                "SELECT s.ServiceID, s.CarID, s.CustomerID, s.ServiceDate, s.ServiceDescription, s.Cost, " +
                        "c.FirstName, c.LastName, c.Email, c.Phone, c.Address, c.City, c.State, c.ZipCode, " +
                        "ca.Make, ca.Model, ca.Year, ca.Price, ca.Stock, ca.VIN " +
                        "FROM services s " +
                        "JOIN customers c ON s.CustomerID = c.CustomerID " +
                        "JOIN cars ca ON s.CarID = ca.CarID WHERE 1=1");

        if (!carID.isEmpty()) {
            query.append(" AND s.CarID = ?");
        }
        if (!customerID.isEmpty()) {
            query.append(" AND s.CustomerID = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (!carID.isEmpty()) {
                pstmt.setInt(paramIndex++, Integer.parseInt(carID));
            }
            if (!customerID.isEmpty()) {
                pstmt.setInt(paramIndex++, Integer.parseInt(customerID));
            }

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Service> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Service(
                        rs.getInt("ServiceID"),
                        rs.getInt("CarID"),
                        rs.getInt("CustomerID"),
                        rs.getDate("ServiceDate").toLocalDate(),
                        rs.getString("ServiceDescription"),
                        rs.getDouble("Cost"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Address"),
                        rs.getString("City"),
                        rs.getString("State"),
                        rs.getString("ZipCode"),
                        rs.getString("Make"),
                        rs.getString("Model"),
                        rs.getInt("Year"),
                        rs.getDouble("Price"),
                        rs.getInt("Stock"),
                        rs.getString("VIN")));
            }
            tableView.setItems(data);

        } catch (SQLException e) {
            lblErrorMessage.setText("Database error occurred. Please try again.");
        } catch (NumberFormatException e) {
            lblErrorMessage.setText("Invalid input. Please ensure ID fields are number.");
        }
    }
}
