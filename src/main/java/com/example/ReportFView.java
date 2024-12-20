package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.*;

public class ReportFView {
    private VBox view;
    private TableView<ServiceDetail> tableView;
    private TextField txtCarID;
    private Button btnGenerate;

    public ReportFView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Input fields
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        txtCarID = new TextField();
        txtCarID.setPromptText("Car ID");

        btnGenerate = ButtonFactory.createActionButton("Generate Report");
        btnGenerate.setOnAction(e -> generateReport());

        inputBox.getChildren().addAll(new Label("Car ID:"), txtCarID, btnGenerate);

        // TableView setup
        tableView = new TableView<>();
        tableView.setPrefHeight(400);

        TableColumn<ServiceDetail, Integer> colServiceID = new TableColumn<>("Service ID");
        colServiceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));

        TableColumn<ServiceDetail, Integer> colCarID = new TableColumn<>("Car ID");
        colCarID.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<ServiceDetail, String> colMake = new TableColumn<>("Make");
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<ServiceDetail, String> colModel = new TableColumn<>("Model");
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<ServiceDetail, Date> colServiceDate = new TableColumn<>("Service Date");
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));

        TableColumn<ServiceDetail, String> colDescription = new TableColumn<>("Description");
        colDescription.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));

        TableColumn<ServiceDetail, Double> colCost = new TableColumn<>("Cost");
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        tableView.getColumns().addAll(colServiceID, colCarID, colMake, colModel, colServiceDate, colDescription,
                colCost);
        tableView.getColumns().forEach(column -> column.setReorderable(false));

        view.getChildren().addAll(inputBox, tableView);
    }

    public VBox getView() {
        return view;
    }

    private void generateReport() {
        String carID = txtCarID.getText().trim();

        if (carID.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Car ID.");
            return;
        }

        String query = "SELECT s.ServiceID, s.CarID, c.Make, c.Model, s.ServiceDate, s.ServiceDescription, s.Cost " +
                "FROM services s " +
                "JOIN cars c ON s.CarID = c.CarID " +
                "WHERE s.CarID = ? " +
                "ORDER BY s.ServiceDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(carID));
            ResultSet rs = pstmt.executeQuery();

            ObservableList<ServiceDetail> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new ServiceDetail(
                        rs.getInt("ServiceID"),
                        rs.getInt("CarID"),
                        rs.getString("Make"),
                        rs.getString("Model"),
                        rs.getDate("ServiceDate"),
                        rs.getString("ServiceDescription"),
                        rs.getDouble("Cost")));
            }
            tableView.setItems(data);

            if (data.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Data", "No service history found for the specified Car ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while fetching the report.");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Input Error", "Car ID must be a valid number.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class to represent the service detail
    public static class ServiceDetail {
        private final int serviceID;
        private final int carID;
        private final String make;
        private final String model;
        private final Date serviceDate;
        private final String serviceDescription;
        private final Double cost;

        public ServiceDetail(int serviceID, int carID, String make, String model, Date serviceDate,
                String serviceDescription, Double cost) {
            this.serviceID = serviceID;
            this.carID = carID;
            this.make = make;
            this.model = model;
            this.serviceDate = serviceDate;
            this.serviceDescription = serviceDescription;
            this.cost = cost;
        }

        public int getServiceID() {
            return serviceID;
        }

        public int getCarID() {
            return carID;
        }

        public String getMake() {
            return make;
        }

        public String getModel() {
            return model;
        }

        public Date getServiceDate() {
            return serviceDate;
        }

        public String getServiceDescription() {
            return serviceDescription;
        }

        public Double getCost() {
            return cost;
        }
    }
}
