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

import java.sql.*;
import java.util.Optional;

public class OrdersSection {
    private TableView<Order> ordersTableView;
    private VBox view;
    private OrderEditForm orderEditForm;
    private OrderAddForm orderAddForm;
    private ObservableList<Order> ordersData;
    

    public OrdersSection() {
        // Initialize forms
        orderEditForm = new OrderEditForm(this);
        orderAddForm = new OrderAddForm(this);
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Create the TableView
        ordersTableView = new TableView<>();
        ordersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTableView.setPrefSize(750, 400);

        // Define columns
        TableColumn<Order, Integer> colOrderID = new TableColumn<>("Order ID");
        colOrderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));

        TableColumn<Order, Date> colOrderDate = new TableColumn<>("Order Date");
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<Order, Integer> colCarID = new TableColumn<>("Car ID");
        colCarID.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<Order, Integer> colCustomerID = new TableColumn<>("Customer ID");
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<Order, Integer> colEmployeeID = new TableColumn<>("Employee ID");
        colEmployeeID.setCellValueFactory(new PropertyValueFactory<>("employeeID"));

        TableColumn<Order, Integer> colQuantity = new TableColumn<>("Quantity");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, Double> colTotalPrice = new TableColumn<>("Total Price");
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Add Edit Icon Column
        TableColumn<Order, Void> colEdit = createEditColumn();


        // Add columns to the TableView
        ordersTableView.getColumns().addAll(colOrderID, colOrderDate, colCarID, colCustomerID,
                colEmployeeID, colQuantity, colTotalPrice, colEdit);
        ordersTableView.getColumns().forEach(column -> column.setReorderable(false));

        // Load data from the database
        ordersData = fetchOrdersData();
        FilteredList<Order> filteredData = new FilteredList<>(ordersData, p -> true);
        ordersTableView.setItems(filteredData);

        // Create search fields
        TextField txtOrderID = new TextField();
        txtOrderID.setPromptText("Order ID");

        TextField txtOrderDate = new TextField();
        txtOrderDate.setPromptText("Order Date");

        TextField txtCarID = new TextField();
        txtCarID.setPromptText("Car ID");

        TextField txtCustomerID = new TextField();
        txtCustomerID.setPromptText("Customer ID");

        TextField txtEmployeeID = new TextField();
        txtEmployeeID.setPromptText("Employee ID");

        TextField txtQuantity = new TextField();
        txtQuantity.setPromptText("Quantity");

        TextField txtTotalPrice = new TextField();
        txtTotalPrice.setPromptText("Total Price");

        // Add filtering logic
        txtOrderID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        txtOrderDate.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        txtCarID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        txtCustomerID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        txtEmployeeID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        txtTotalPrice.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                order -> filterOrders(order, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                        txtQuantity, txtTotalPrice)));

        // Reset button to clear all text fields
        Button btnReset =ButtonFactory.createActionButton("Reset");
        btnReset.setOnAction(e -> {
            txtOrderID.clear();
            txtOrderDate.clear();
            txtCarID.clear();
            txtCustomerID.clear();
            txtEmployeeID.clear();
            txtQuantity.clear();
            txtTotalPrice.clear();
        });

        

        // Layout for search fields with label and reset button
        Label lblSearch = new Label("Search:");
        HBox searchFields = new HBox(15, lblSearch, txtOrderID, txtOrderDate, txtCarID, txtCustomerID, txtEmployeeID,
                txtQuantity, txtTotalPrice);
        searchFields.setAlignment(Pos.CENTER_LEFT);
        searchFields.setPadding(new Insets(5));

        // Add Buttons for Adding Orders
        Button btnAddOrder = ButtonFactory.createActionButton("Add Order");
        btnAddOrder.setOnAction(e -> {
            hideEditForm(); // Hide edit form if visible
            showAddForm();
        });

        HBox hboxButtons = new HBox(10, btnAddOrder, btnReset);
        hboxButtons.setAlignment(Pos.CENTER);
        hboxButtons.setPadding(new Insets(10));

        // Add everything to the main view
        view.getChildren().addAll(lblSearch, searchFields, hboxButtons, ordersTableView);
    }

    private TableColumn<Order, Void> createEditColumn() {
        TableColumn<Order, Void> colEdit = new TableColumn<>("Edit");
        colEdit.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button editButton = ButtonFactory.createEditButton();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    hideAddForm();
                    showEditForm(order);
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

   

    private ObservableList<Order> fetchOrdersData() {
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM orders")) {

            while (rs.next()) {
                ordersList.add(new Order(
                        rs.getInt("OrderID"),
                        rs.getDate("OrderDate"),
                        rs.getInt("CarID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalPrice")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersList;
    }

    private boolean filterOrders(Order order, TextField txtOrderID, TextField txtOrderDate, TextField txtCarID,
            TextField txtCustomerID, TextField txtEmployeeID, TextField txtQuantity, TextField txtTotalPrice) {
        boolean matchesOrderID = txtOrderID.getText().isEmpty()
                || String.valueOf(order.getOrderID()).equals(txtOrderID.getText());
        boolean matchesOrderDate = txtOrderDate.getText().isEmpty()
                || order.getOrderDate().toString().contains(txtOrderDate.getText());
        boolean matchesCarID = txtCarID.getText().isEmpty()
                || String.valueOf(order.getCarID()).equals(txtCarID.getText());
        boolean matchesCustomerID = txtCustomerID.getText().isEmpty()
                || String.valueOf(order.getCustomerID()).equals(txtCustomerID.getText());
        boolean matchesEmployeeID = txtEmployeeID.getText().isEmpty()
                || String.valueOf(order.getEmployeeID()).equals(txtEmployeeID.getText());
        boolean matchesQuantity = txtQuantity.getText().isEmpty()
                || String.valueOf(order.getQuantity()).equals(txtQuantity.getText());
        boolean matchesTotalPrice = txtTotalPrice.getText().isEmpty()
                || String.valueOf(order.getTotalPrice()).equals(txtTotalPrice.getText());

        return matchesOrderID && matchesOrderDate && matchesCarID && matchesCustomerID && matchesEmployeeID &&
                matchesQuantity && matchesTotalPrice;
    }

    private void showEditForm(Order order) {
        orderEditForm.showEditForm(order);
        if (!view.getChildren().contains(orderEditForm.getForm())) {
            view.getChildren().add(orderEditForm.getForm());
        }
    }

    private void showAddForm() {
        orderAddForm.showAddForm();
        if (!view.getChildren().contains(orderAddForm.getForm())) {
            view.getChildren().add(orderAddForm.getForm());
        }
    }

    
    public void refreshData() {
        ordersData = fetchOrdersData();
        ordersTableView.setItems(ordersData);
    }
    public void reloadScene() {
        // Get the parent layout (CarShopGUI's main content)
        VBox parent = (VBox) view.getParent();
        if (parent != null) {
            OrdersSection refreshedOrdersSection = new OrdersSection();
            parent.getChildren().setAll(refreshedOrdersSection.getView());
        }
    }
    public void hideEditForm() {
        view.getChildren().remove(orderEditForm.getForm());
    }

    public void hideAddForm() {
        view.getChildren().remove(orderAddForm.getForm());
    }

    public VBox getView() {
        return view;
    }
}
