package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.*;

public class ReportEView {
    private VBox view;
    private TableView<ServiceFrequency> tableView;
    private ComboBox<String> cmbReportType;
    private Button btnGenerate;

    public ReportEView() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Selection for report type
        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        cmbReportType = new ComboBox<>();
        cmbReportType.getItems().addAll("Car Model", "Customer Category (State)", "Customer Category (City)");
        cmbReportType.setValue("Car Model"); // Default selection

        btnGenerate = ButtonFactory.createActionButton("Generate Report");
        btnGenerate.setOnAction(e -> generateReport());

        selectionBox.getChildren().addAll(new Label("Report Type:"), cmbReportType, btnGenerate);

        // TableView setup
        tableView = new TableView<>();
        tableView.setPrefHeight(400);

        TableColumn<ServiceFrequency, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<ServiceFrequency, Integer> colFrequency = new TableColumn<>("Service Frequency");
        colFrequency.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        tableView.getColumns().addAll(colCategory, colFrequency);
        tableView.getColumns().forEach(column -> column.setReorderable(false));

        view.getChildren().addAll(selectionBox, tableView);
    }

    public VBox getView() {
        return view;
    }

    private void generateReport() {
        String reportType = cmbReportType.getValue();

        String query;
        if (reportType.equals("Car Model")) {
            query = "SELECT c.Model AS Category, COUNT(s.ServiceID) AS Frequency " +
                    "FROM services s " +
                    "JOIN cars c ON s.CarID = c.CarID " +
                    "GROUP BY c.Model " +
                    "ORDER BY Frequency DESC";
        } else if (reportType.equals("Customer Category (State)")) {
            query = "SELECT cu.State AS Category, COUNT(s.ServiceID) AS Frequency " +
                    "FROM services s " +
                    "JOIN customers cu ON s.CustomerID = cu.CustomerID " +
                    "GROUP BY cu.State " +
                    "ORDER BY Frequency DESC";
        } else if (reportType.equals("Customer Category (City)")) {
            query = "SELECT cu.City AS Category, COUNT(s.ServiceID) AS Frequency " +
                    "FROM services s " +
                    "JOIN customers cu ON s.CustomerID = cu.CustomerID " +
                    "GROUP BY cu.City " +
                    "ORDER BY Frequency DESC";
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Invalid report type selected.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ObservableList<ServiceFrequency> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new ServiceFrequency(
                        rs.getString("Category"),
                        rs.getInt("Frequency")
                ));
            }
            tableView.setItems(data);

            if (data.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Data", "No services found for the selected report type.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while generating the report.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class to represent the service frequency
    public static class ServiceFrequency {
        private final String category;
        private final Integer frequency;

        public ServiceFrequency(String category, Integer frequency) {
            this.category = category;
            this.frequency = frequency;
        }

        public String getCategory() {
            return category;
        }

        public Integer getFrequency() {
            return frequency;
        }
    }
}
