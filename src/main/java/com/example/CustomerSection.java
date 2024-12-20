package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerSection {
    private TableView<Customer> customerTableView;
    private VBox view;
    private CustomerEditForm customerEditForm;
    private CustomerAddForm customerAddForm;
    private ObservableList<Customer> customerData;

    public CustomerSection() {
        
        // Initialize forms
        customerEditForm = new CustomerEditForm(this);
        customerAddForm = new CustomerAddForm(this);
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Create the TableView
        customerTableView = new TableView<>();
        customerTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customerTableView.setPrefSize(750, 400);

        // Define columns
        TableColumn<Customer, Integer> colCustomerID = new TableColumn<>("Customer ID");
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<Customer, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Customer, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Customer, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setMinWidth(50);

    
        TableColumn<Customer, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Customer, String> colAddress = new TableColumn<>("Address");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, String> colCity = new TableColumn<>("City");
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<Customer, String> colState = new TableColumn<>("State");
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));

        TableColumn<Customer, String> colZipCode = new TableColumn<>("Zip Code");
        colZipCode.setCellValueFactory(new PropertyValueFactory<>("zipCode"));

        // Add Edit Icon Column
        TableColumn<Customer, Void> colEdit = createEditColumn();
       

        // Add columns to the TableView
        customerTableView.getColumns().addAll(colCustomerID, colFirstName, colLastName, colEmail, colPhone, colAddress,
                colCity, colState, colZipCode, colEdit);
        customerTableView.getColumns().forEach(column -> column.setReorderable(false));

        // Load data from the database
        customerData = fetchCustomerData();
        FilteredList<Customer> filteredData = new FilteredList<>(customerData, p -> true);
        customerTableView.setItems(filteredData);

        // Create search fields
        TextField txtCustomerID = new TextField();
        txtCustomerID.setPromptText("Customer ID");

        TextField txtFirstName = new TextField();
        txtFirstName.setPromptText("First Name");

        TextField txtLastName = new TextField();
        txtLastName.setPromptText("Last Name");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");

        TextField txtPhone = new TextField();
        txtPhone.setPromptText("Phone");

        TextField txtAddress = new TextField();
        txtAddress.setPromptText("Address");

        TextField txtCity = new TextField();
        txtCity.setPromptText("City");

        TextField txtState = new TextField();
        txtState.setPromptText("State");

        TextField txtZipCode = new TextField();
        txtZipCode.setPromptText("Zip Code");
        

        // Add filtering logic
        txtCustomerID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtLastName.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtCity.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtState.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        txtZipCode.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                customer -> filterCustomers(customer, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                        txtAddress, txtCity, txtState, txtZipCode)));

        // Reset button to clear all text fields
        Button btnReset = ButtonFactory.createActionButton("Reset");
        btnReset.setOnAction(e -> {
            txtCustomerID.clear();
            txtFirstName.clear();
            txtLastName.clear();
            txtEmail.clear();
            txtPhone.clear();
            txtAddress.clear();
            txtCity.clear();
            txtState.clear();
            txtZipCode.clear();
        });

      

        // Layout for search fields with label and reset button
        Label lblSearch = new Label("Search:");
        HBox searchFields = new HBox(8, lblSearch, txtCustomerID, txtFirstName, txtLastName, txtEmail, txtPhone,
                txtAddress, txtCity, txtState, txtZipCode);
        searchFields.setAlignment(Pos.CENTER_LEFT);
        searchFields.setPadding(new Insets(5));

        // Add Buttons for Adding Customers
        Button btnAddCustomer = ButtonFactory.createActionButton("Add Customer");
        btnAddCustomer.setOnAction(e -> {
            hideEditForm(); // Hide edit form if visible
            showAddForm();
        });

        HBox hboxButtons = new HBox(10, btnAddCustomer, btnReset);
        hboxButtons.setAlignment(Pos.CENTER);
        hboxButtons.setPadding(new Insets(10));

        // Add everything to the main view
        view.getChildren().addAll(lblSearch, searchFields, hboxButtons, customerTableView);
    }

    private TableColumn<Customer, Void> createEditColumn() {
        TableColumn<Customer, Void> colEdit = new TableColumn<>("Edit");
        colEdit.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button editButton = ButtonFactory.createEditButton();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    hideAddForm(); // Hide the Add Form when Edit is clicked
                    showEditForm(customer);
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

    private ObservableList<Customer> fetchCustomerData() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Customers")) {

            while (rs.next()) {
                customerList.add(new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Address"),
                        rs.getString("City"),
                        rs.getString("State"),
                        rs.getString("ZipCode")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    private boolean filterCustomers(Customer customer, TextField txtCustomerID, TextField txtFirstName,
            TextField txtLastName, TextField txtEmail, TextField txtPhone,
            TextField txtAddress, TextField txtCity, TextField txtState,
            TextField txtZipCode) {
        boolean matchesCustomerID = txtCustomerID.getText().isEmpty()
                || String.valueOf(customer.getCustomerID()).equals(txtCustomerID.getText());
        boolean matchesFirstName = txtFirstName.getText().isEmpty()
                || customer.getFirstName().toLowerCase().contains(txtFirstName.getText().toLowerCase());
        boolean matchesLastName = txtLastName.getText().isEmpty()
                || customer.getLastName().toLowerCase().contains(txtLastName.getText().toLowerCase());
        boolean matchesEmail = txtEmail.getText().isEmpty()
                || customer.getEmail().toLowerCase().contains(txtEmail.getText().toLowerCase());
        boolean matchesPhone = txtPhone.getText().isEmpty()
                || customer.getPhone().toLowerCase().contains(txtPhone.getText().toLowerCase());
        boolean matchesAddress = txtAddress.getText().isEmpty()
                || customer.getAddress().toLowerCase().contains(txtAddress.getText().toLowerCase());
        boolean matchesCity = txtCity.getText().isEmpty()
                || customer.getCity().toLowerCase().contains(txtCity.getText().toLowerCase());
        boolean matchesState = txtState.getText().isEmpty()
                || customer.getState().toLowerCase().contains(txtState.getText().toLowerCase());
        boolean matchesZipCode = txtZipCode.getText().isEmpty()
                || customer.getZipCode().toLowerCase().contains(txtZipCode.getText().toLowerCase());
        return matchesCustomerID && matchesFirstName && matchesLastName && matchesEmail && matchesPhone &&
                matchesAddress && matchesCity && matchesState && matchesZipCode;
    }

    private void showEditForm(Customer customer) {
        customerEditForm.showEditForm(customer);
        if (!view.getChildren().contains(customerEditForm.getForm())) {
            view.getChildren().add(customerEditForm.getForm());
        }
    }

    private void showAddForm() {
        customerAddForm.showAddForm();
        if (!view.getChildren().contains(customerAddForm.getForm())) {
            view.getChildren().add(customerAddForm.getForm());
        }
    }

    public void refreshData() {
        customerData = fetchCustomerData();
        customerTableView.setItems(customerData);
    }

    public void hideEditForm() {
        view.getChildren().remove(customerEditForm.getForm());
    }

    public void hideAddForm() {
        view.getChildren().remove(customerAddForm.getForm());
    }

    public void reloadScene() {
        // Get the parent layout (CarShopGUI's main content)
        VBox parent = (VBox) view.getParent();
        if (parent != null) {
            CustomerSection refreshedCustomerSection = new CustomerSection();
            parent.getChildren().setAll(refreshedCustomerSection.getView());
        }
    }

    public VBox getView() {
        return view;
    }
}
