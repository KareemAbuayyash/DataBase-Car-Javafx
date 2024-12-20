package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class Sidebar {
    private VBox sidebar;
    private Button btnCars;
    private Button btnCustomers;
    private Button btnEmployees;
    private Button btnOrders;
    private Button btnPayments;
    private Button btnServices;
    private Button btnReports;
    private Button btnLogout;

    public Sidebar(String username) {
        sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar"); // Add the CSS class
        sidebar.setPadding(new Insets(10));

        // User label
        FontIcon userIcon = new FontIcon("fas-user");
        userIcon.setIconSize(20);
        userIcon.setIconColor(javafx.scene.paint.Color.WHITE);
        Label userLabel = new Label(" " + username);
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox userBox = new HBox(5, userIcon, userLabel);
        userBox.setAlignment(Pos.CENTER_LEFT);

        // Buttons
        btnCars = ButtonFactory.createSidebarButton(" Cars", "fas-car");
        btnCustomers = ButtonFactory.createSidebarButton("Customers", "fas-users");
        btnEmployees = ButtonFactory.createSidebarButton("  Employees", "fas-user-tie");
        btnOrders = ButtonFactory.createSidebarButton("   Orders", "fas-file-invoice-dollar");
        btnPayments = ButtonFactory.createSidebarButton(" Payments", "fas-hand-holding-usd");
        btnServices = ButtonFactory.createSidebarButton(" Services", "fas-tools");
        btnReports = ButtonFactory.createSidebarButton(" Reports", "fas-chart-line");
        btnLogout = ButtonFactory.createSidebarButton(" Logout", "fas-sign-out-alt");

        sidebar.getChildren().addAll(userBox, btnCars, btnCustomers, btnEmployees, btnOrders, btnPayments, btnServices,
                btnReports, btnLogout);
    }

    // Getter methods for buttons
    public VBox getSidebar() {
        return sidebar;
    }

    public Button getBtnCars() {
        return btnCars;
    }

    public Button getBtnCustomers() {
        return btnCustomers;
    }

    public Button getBtnEmployees() {
        return btnEmployees;
    }

    public Button getBtnOrders() {
        return btnOrders;
    }

    public Button getBtnPayments() {
        return btnPayments;
    }

    public Button getBtnServices() {
        return btnServices;
    }

    public Button getBtnReports() {
        return btnReports;
    }

    public Button getBtnLogout() {
        return btnLogout;
    }

  
    
}
