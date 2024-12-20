package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class ServicesSection {
    private TableView<Service> servicesTableView;
    private VBox view;
    private ServiceEditForm serviceEditForm;
    private ServiceAddForm serviceAddForm;
    private ObservableList<Service> serviceData;

    public ServicesSection() {
        // Initialize forms
        serviceEditForm = new ServiceEditForm(this);
        serviceAddForm = new ServiceAddForm(this);

        // Create the TableView
        servicesTableView = new TableView<>();
        servicesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        servicesTableView.setPrefSize(750, 400);

        // Define columns
        TableColumn<Service, Integer> colServiceID = new TableColumn<>("Service ID");
        colServiceID.setCellValueFactory(new PropertyValueFactory<>("serviceID"));

        TableColumn<Service, Integer> colCarID = new TableColumn<>("Car ID");
        colCarID.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<Service, Integer> colCustomerID = new TableColumn<>("Customer ID");
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<Service, LocalDate> colServiceDate = new TableColumn<>("Service Date");
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));

        TableColumn<Service, String> colServiceDescription = new TableColumn<>("Service Description");
        colServiceDescription.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));

        TableColumn<Service, Double> colCost = new TableColumn<>("Cost");
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        // Add Edit Icon Column
        TableColumn<Service, Void> colEdit = createEditColumn();

       

        servicesTableView.getColumns().addAll(
                colServiceID, colCarID, colCustomerID,
                colServiceDate, colServiceDescription, colCost, colEdit );

        // Load data from the database
        serviceData = fetchServiceData();
        FilteredList<Service> filteredData = new FilteredList<>(serviceData, p -> true);
        servicesTableView.setItems(filteredData);

        // Create search fields
        TextField txtServiceID = new TextField();
        txtServiceID.setPromptText("Service ID");

        TextField txtCarID = new TextField();
        txtCarID.setPromptText("Car ID");

        TextField txtCustomerID = new TextField();
        txtCustomerID.setPromptText("Customer ID");

        TextField txtServiceDate = new TextField();
        txtServiceDate.setPromptText("Service Date");

        TextField txtServiceDescription = new TextField();
        txtServiceDescription.setPromptText("Service Description");

        TextField txtCost = new TextField();
        txtCost.setPromptText("Cost");
        
        // Add filtering logic
        txtServiceID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                service -> filterServices(service, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost)));

        txtCarID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                service -> filterServices(service, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost)));

        txtCustomerID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                service -> filterServices(service, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost)));

        txtServiceDate.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                service -> filterServices(service, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost)));

        txtServiceDescription.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                service -> filterServices(service, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost)));

        txtCost.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                service -> filterServices(service, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost)));

        // Reset button to clear all text fields
        Button btnReset = ButtonFactory.createActionButton("Reset");
        btnReset.setOnAction(e -> {
            txtServiceID.clear();
            txtCarID.clear();
            txtCustomerID.clear();
            txtServiceDate.clear();
            txtServiceDescription.clear();
            txtCost.clear();
        });

      

        // Layout for search fields with label and reset button
        Label lblSearch = new Label("Search:");
        HBox searchFields = new HBox(15, lblSearch, txtServiceID, txtCarID, txtCustomerID, txtServiceDate, txtServiceDescription, txtCost, btnReset);
        searchFields.setAlignment(Pos.CENTER_LEFT);
        searchFields.setPadding(new Insets(5));

        // Add Buttons for Adding Services
        Button btnAddService = ButtonFactory.createActionButton("Add Service");
        btnAddService.setOnAction(e -> {
            hideEditForm(); // Hide edit form if visible
            showAddForm();
        });

        HBox hboxButtons = new HBox(10, btnAddService);
        hboxButtons.setAlignment(Pos.CENTER);
        hboxButtons.setPadding(new Insets(10));

        // Add everything to the main view
        view = new VBox(10);
        view.setPadding(new Insets(10));
        view.getChildren().addAll(lblSearch, searchFields, hboxButtons, servicesTableView);
    }

    private boolean filterServices(Service service, TextField txtServiceID, TextField txtCarID, TextField txtCustomerID,
                                   TextField txtServiceDate, TextField txtServiceDescription, TextField txtCost) {
        boolean matchesServiceID = txtServiceID.getText().isEmpty()
                || String.valueOf(service.getServiceID()).equals(txtServiceID.getText());
        boolean matchesCarID = txtCarID.getText().isEmpty()
                || String.valueOf(service.getCarID()).equals(txtCarID.getText());
        boolean matchesCustomerID = txtCustomerID.getText().isEmpty()
                || String.valueOf(service.getCustomerID()).equals(txtCustomerID.getText());
        boolean matchesServiceDate = txtServiceDate.getText().isEmpty()
                || service.getServiceDate().toString().contains(txtServiceDate.getText());
        boolean matchesServiceDescription = txtServiceDescription.getText().isEmpty()
                || service.getServiceDescription().toLowerCase().contains(txtServiceDescription.getText().toLowerCase());
        boolean matchesCost = txtCost.getText().isEmpty()
                || String.valueOf(service.getCost()).contains(txtCost.getText());

        return matchesServiceID && matchesCarID && matchesCustomerID && matchesServiceDate && matchesServiceDescription && matchesCost;
    }

    private TableColumn<Service, Void> createEditColumn() {
        TableColumn<Service, Void> colEdit = new TableColumn<>("Edit");
        colEdit.setCellFactory(param -> new TableCell<Service, Void>() {
            private final Button editButton = ButtonFactory.createEditButton();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    Service service = getTableView().getItems().get(getIndex());
                    hideAddForm(); // Hide the Add Form when Edit is clicked
                    showEditForm(service);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
                setAlignment(Pos.CENTER);
            }
        });
        return colEdit;
    }

 

    private ObservableList<Service> fetchServiceData() {
        ObservableList<Service> serviceList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM services")) {

            while (rs.next()) {
                serviceList.add(new Service(
                        rs.getInt("ServiceID"),
                        rs.getInt("CarID"),
                        rs.getInt("CustomerID"),
                        rs.getDate("ServiceDate").toLocalDate(),
                        rs.getString("ServiceDescription"),
                        rs.getDouble("Cost")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceList;
    }

    public void refreshData() {
        serviceData = fetchServiceData();
        servicesTableView.setItems(serviceData);
    }
    public void reloadScene() {
        // Get the parent layout (CarShopGUI's main content)
        VBox parent = (VBox) view.getParent();
        if (parent != null) {
            ServicesSection refreshedServicesSection = new ServicesSection();
            parent.getChildren().setAll(refreshedServicesSection.getView());
        }
    }
    public void hideEditForm() {
        view.getChildren().remove(serviceEditForm.getForm());
    }

    public void hideAddForm() {
        view.getChildren().remove(serviceAddForm.getForm());
    }

    public VBox getView() {
        return view;
    }

    private void showEditForm(Service service) {
        serviceEditForm.showEditForm(service);
        if (!view.getChildren().contains(serviceEditForm.getForm())) {
            view.getChildren().add(serviceEditForm.getForm());
        }
    }

    private void showAddForm() {
        serviceAddForm.showAddForm();
        if (!view.getChildren().contains(serviceAddForm.getForm())) {
            view.getChildren().add(serviceAddForm.getForm());
        }
    }
}
