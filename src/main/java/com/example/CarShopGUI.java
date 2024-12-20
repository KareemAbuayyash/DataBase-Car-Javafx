package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CarShopGUI extends Application {
    private String username;
    private VBox mainContent;

    public CarShopGUI() {
        this.username = ""; // Default username if not provided
    }

    public CarShopGUI(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the sidebar
        Sidebar sidebar = new Sidebar(username);

        // Create the header with title and username
        HBox header = new HBox();
        Label titleLabel = new Label("Kareem Garage");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Label usernameLabel = new Label("Hello, " + username);
        usernameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        usernameLabel.setStyle("-fx-text-fill: white;");
        
        header.getChildren().addAll(titleLabel, usernameLabel);
        header.setSpacing(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header"); 

        
        header.setMaxWidth(Double.MAX_VALUE);  
        HBox.setHgrow(header, Priority.ALWAYS); 

        // Main content area
        mainContent = new VBox();
        mainContent.setPadding(new Insets(10));
        mainContent.setSpacing(10);
        mainContent.getChildren().add(new Label("Select a Section"));

        // Create a VBox to hold the header and main content
        VBox contentVBox = new VBox();
        contentVBox.getChildren().addAll(header, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Create the main layout with sidebar and content
        HBox mainLayout = new HBox();
        mainLayout.setSpacing(0); // No spacing between sidebar and content
        mainLayout.getChildren().addAll(sidebar.getSidebar(), contentVBox); // Sidebar first, then content

        // Ensure sidebar has a fixed width
        sidebar.getSidebar().setMinWidth(200); // Fixed width for sidebar
        sidebar.getSidebar().setMaxWidth(200); // Fixed width for sidebar
        // Ensure contentVBox grows to fill available width
        HBox.setHgrow(contentVBox, Priority.ALWAYS); // Allow contentVBox to grow and take up remaining space

        // Event handling for sidebar buttons
        sidebar.getBtnCars().setOnAction(e -> showCarSection());
        sidebar.getBtnCustomers().setOnAction(e -> showCustomerSection());
        sidebar.getBtnEmployees().setOnAction(e -> showEmployeeSection());
        sidebar.getBtnOrders().setOnAction(e -> showOrdersSection());
        sidebar.getBtnPayments().setOnAction(e -> showPaymentsSection());
        sidebar.getBtnServices().setOnAction(e -> showServicesSection());
        sidebar.getBtnReports().setOnAction(e -> showReportsSection());
        sidebar.getBtnLogout().setOnAction(e -> primaryStage.close());

        // Scene setup
        Scene scene = new Scene(mainLayout);
        //,ake secne full screen
        primaryStage.setMaximized(true);

        // Link the CSS file
        scene.getStylesheets().add(getClass().getResource("/com/example/styles.css").toExternalForm());


        primaryStage.setTitle("Car Shop System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Methods to show content for each section
    private void showCarSection() {
        CarSection carSection = new CarSection();
        mainContent.getChildren().setAll(carSection.getView());
    }

    private void showCustomerSection() {
        CustomerSection customerSection = new CustomerSection();
        mainContent.getChildren().setAll(customerSection.getView());
    }

    private void showEmployeeSection() {
        EmployeeSection employeeSection = new EmployeeSection();
        mainContent.getChildren().setAll(employeeSection.getView());
    }

    private void showOrdersSection() {
        OrdersSection ordersSection = new OrdersSection();
        mainContent.getChildren().setAll(ordersSection.getView());
    }

    private void showPaymentsSection() {
        PaymentsSection paymentsSection = new PaymentsSection();
        mainContent.getChildren().setAll(paymentsSection.getView());
    }

    private void showServicesSection() {
        ServicesSection servicesSection = new ServicesSection();
        mainContent.getChildren().setAll(servicesSection.getView());
    }

    private void showReportsSection() {
        ReportsSection reportsSection = new ReportsSection();
        mainContent.getChildren().setAll(reportsSection.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setMaximized(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMaximized'");
    }
}
