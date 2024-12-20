package com.example;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDView {
    private VBox view;
    private TableView<Revenue> tableView;
    private TableView<MonthlyService> serviceCountTableView;  // New table for service count
    private ComboBox<String> periodSelector;
    private Button btnGenerate;

    public ReportDView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Input fields
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        periodSelector = new ComboBox<>();
        periodSelector.getItems().addAll("Monthly", "Quarterly");
        periodSelector.setPromptText("Select Period");

        btnGenerate = new Button("Generate Report");
        btnGenerate.setOnAction(e -> generateReport());

        inputBox.getChildren().addAll(new Label("Select Period:"), periodSelector, btnGenerate);

        // TableView setup for revenue data
        tableView = new TableView<>();
        tableView.setPrefHeight(200);

        TableColumn<Revenue, String> colDate = new TableColumn<>("Date/Quarter");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Revenue, String> colServiceType = new TableColumn<>("Service Type");
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));

        TableColumn<Revenue, Double> colRevenue = new TableColumn<>("Revenue");
        colRevenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        tableView.getColumns().addAll(colDate, colServiceType, colRevenue);

        // New table for service count data
        serviceCountTableView = new TableView<>();
        serviceCountTableView.setPrefHeight(200);

        TableColumn<MonthlyService, String> colServiceDate = new TableColumn<>("Date/Quarter");
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<MonthlyService, String> colServiceDescription = new TableColumn<>("Service Type");
        colServiceDescription.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));

        TableColumn<MonthlyService, Integer> colServiceCount = new TableColumn<>("Service Count");
        colServiceCount.setCellValueFactory(new PropertyValueFactory<>("serviceCount"));

        serviceCountTableView.getColumns().addAll(colServiceDate, colServiceDescription, colServiceCount);

        // Add both tables to the layout
        view.getChildren().addAll(inputBox, tableView, serviceCountTableView);
    }

    public VBox getView() {
        return view;
    }

    private void generateReport() {
        String selectedPeriod = periodSelector.getValue();

        if (selectedPeriod == null) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please select a period.");
            return;
        }

        String query;
        if (selectedPeriod.equals("Monthly")) {
            query = "SELECT DATE_FORMAT(s.ServiceDate, '%Y-%m-%d') AS Date, s.ServiceDescription AS ServiceType, SUM(s.Cost) AS Revenue " +
                    "FROM car.services s " +
                    "GROUP BY DATE_FORMAT(s.ServiceDate, '%Y-%m-%d'), s.ServiceDescription " +
                    "ORDER BY Date, s.ServiceDescription";
        } else { // Quarterly
            query = "SELECT CONCAT('Q', QUARTER(s.ServiceDate), '-', YEAR(s.ServiceDate)) AS Quarter, s.ServiceDescription AS ServiceType, SUM(s.Cost) AS Revenue " +
                    "FROM car.services s " +
                    "GROUP BY QUARTER(s.ServiceDate), YEAR(s.ServiceDate), s.ServiceDescription " +
                    "ORDER BY Quarter, s.ServiceDescription";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            ObservableList<Revenue> data = FXCollections.observableArrayList();
            while (rs.next()) {
                String period;
                if (selectedPeriod.equals("Monthly")) {
                    period = rs.getString("Date");  // Full Date for Monthly
                } else {  // For Quarterly, show the quarter (e.g., Q1-2024)
                    period = rs.getString("Quarter");
                }

                data.add(new Revenue(
                        period,
                        rs.getString("ServiceType"),
                        rs.getDouble("Revenue")));
            }
            tableView.setItems(data);

            // Fetch service count data for the selected period
            fetchServiceCount(conn, selectedPeriod);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    private void fetchServiceCount(Connection conn, String selectedPeriod) throws SQLException {
        String query;
        if (selectedPeriod.equals("Monthly")) {
            query = "SELECT DATE_FORMAT(s.ServiceDate, '%Y-%m-%d') AS Date, s.ServiceDescription, COUNT(s.ServiceID) AS ServiceCount " +
                    "FROM car.services s " +
                    "GROUP BY DATE_FORMAT(s.ServiceDate, '%Y-%m-%d'), s.ServiceDescription " +
                    "ORDER BY Date, s.ServiceDescription";
        } else { // Quarterly
            query = "SELECT CONCAT('Q', QUARTER(s.ServiceDate), '-', YEAR(s.ServiceDate)) AS Quarter, s.ServiceDescription, COUNT(s.ServiceID) AS ServiceCount " +
                    "FROM car.services s " +
                    "GROUP BY QUARTER(s.ServiceDate), YEAR(s.ServiceDate), s.ServiceDescription " +
                    "ORDER BY Quarter, s.ServiceDescription";
        }

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        ObservableList<MonthlyService> serviceCountData = FXCollections.observableArrayList();
        while (rs.next()) {
            String period;
            if (selectedPeriod.equals("Monthly")) {
                period = rs.getString("Date");  // Full Date for Monthly
            } else {  // For Quarterly, show the quarter (e.g., Q1-2024)
                period = rs.getString("Quarter");
            }

            serviceCountData.add(new MonthlyService(
                    period,
                    rs.getString("ServiceDescription"),
                    rs.getInt("ServiceCount")));
        }
        serviceCountTableView.setItems(serviceCountData);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
