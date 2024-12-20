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

public class PaymentsSection {
    private TableView<Payment> paymentsTableView;
    private VBox view;
    private PaymentEditForm paymentEditForm;
    private PaymentAddForm paymentAddForm;
    private ObservableList<Payment> paymentsData;

    public PaymentsSection() {
        // Initialize forms
        paymentEditForm = new PaymentEditForm(this);
        paymentAddForm = new PaymentAddForm(this);
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Create the TableView
        paymentsTableView = new TableView<>();
        paymentsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        paymentsTableView.setPrefSize(750, 400);

        // Define columns
        TableColumn<Payment, Integer> colPaymentID = new TableColumn<>("Payment ID");
        colPaymentID.setCellValueFactory(new PropertyValueFactory<>("paymentID"));

        TableColumn<Payment, Integer> colOrderID = new TableColumn<>("Order ID");
        colOrderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));

        TableColumn<Payment, LocalDate> colPaymentDate = new TableColumn<>("Payment Date");
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        TableColumn<Payment, String> colPaymentMethod = new TableColumn<>("Payment Method");
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<Payment, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Add Edit Icon Column
        TableColumn<Payment, Void> colEdit = createEditColumn();

        

        paymentsTableView.getColumns().addAll(
                colPaymentID, colOrderID, colPaymentDate,
                colPaymentMethod, colAmount, colEdit);
        paymentsTableView.getColumns().forEach(column -> column.setReorderable(false));

        // Load data from the database
        paymentsData = fetchPaymentData();
        FilteredList<Payment> filteredData = new FilteredList<>(paymentsData, p -> true);
        paymentsTableView.setItems(filteredData);

        // Create search fields
        TextField txtPaymentID = new TextField();
        txtPaymentID.setPromptText("Payment ID");

        TextField txtOrderID = new TextField();
        txtOrderID.setPromptText("Order ID");

        TextField txtPaymentDate = new TextField();
        txtPaymentDate.setPromptText("Payment Date");

        TextField txtPaymentMethod = new TextField();
        txtPaymentMethod.setPromptText("Payment Method");

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Amount");

        // Add filtering logic
        txtPaymentID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                payment -> filterPayments(payment, txtPaymentID, txtOrderID, txtPaymentDate, txtPaymentMethod, txtAmount)));

        txtOrderID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                payment -> filterPayments(payment, txtPaymentID, txtOrderID, txtPaymentDate, txtPaymentMethod, txtAmount)));

        txtPaymentDate.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                payment -> filterPayments(payment, txtPaymentID, txtOrderID, txtPaymentDate, txtPaymentMethod, txtAmount)));

        txtPaymentMethod.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                payment -> filterPayments(payment, txtPaymentID, txtOrderID, txtPaymentDate, txtPaymentMethod, txtAmount)));

        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                payment -> filterPayments(payment, txtPaymentID, txtOrderID, txtPaymentDate, txtPaymentMethod, txtAmount)));

        // Reset button to clear all text fields
        Button btnReset = ButtonFactory.createActionButton("Reset");
        btnReset.setOnAction(e -> {
            txtPaymentID.clear();
            txtOrderID.clear();
            txtPaymentDate.clear();
            txtPaymentMethod.clear();
            txtAmount.clear();
        });


        // Layout for search fields with label and reset button
        Label lblSearch = new Label("Search:");
        HBox searchFields = new HBox(15, lblSearch, txtPaymentID, txtOrderID, txtPaymentDate, txtPaymentMethod, txtAmount);
        searchFields.setAlignment(Pos.CENTER_LEFT);
        searchFields.setPadding(new Insets(5));

        // Add Buttons for Adding Payments
        Button btnAddPayment = ButtonFactory.createActionButton("Add Payment");
        btnAddPayment.setOnAction(e -> {
            hideEditForm(); // Hide edit form if visible
            showAddForm();
        });

        HBox hboxButtons = new HBox(10, btnAddPayment, btnReset);
        hboxButtons.setAlignment(Pos.CENTER);
        hboxButtons.setPadding(new Insets(10));

        // Add everything to the main view
        view.getChildren().addAll(lblSearch, searchFields, hboxButtons, paymentsTableView);
    }

    private TableColumn<Payment, Void> createEditColumn() {
        TableColumn<Payment, Void> colEdit = new TableColumn<>("Edit");
        colEdit.setCellFactory(param -> new TableCell<Payment, Void>() {
            private final Button editButton = ButtonFactory.createEditButton();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    hideAddForm(); // Hide the Add Form when Edit is clicked
                    showEditForm(payment);
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



    private ObservableList<Payment> fetchPaymentData() {
        ObservableList<Payment> paymentList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM payments")) {

            while (rs.next()) {
                paymentList.add(new Payment(
                        rs.getInt("PaymentID"),
                        rs.getInt("OrderID"),
                        rs.getDate("PaymentDate").toLocalDate(),
                        rs.getString("PaymentMethod"),
                        rs.getDouble("Amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentList;
    }

    private boolean filterPayments(Payment payment, TextField txtPaymentID, TextField txtOrderID, TextField txtPaymentDate,
                                   TextField txtPaymentMethod, TextField txtAmount) {
        boolean matchesPaymentID = txtPaymentID.getText().isEmpty()
                || String.valueOf(payment.getPaymentID()).equals(txtPaymentID.getText());
        boolean matchesOrderID = txtOrderID.getText().isEmpty()
                || String.valueOf(payment.getOrderID()).equals(txtOrderID.getText());
        boolean matchesPaymentDate = txtPaymentDate.getText().isEmpty()
                || payment.getPaymentDate().toString().contains(txtPaymentDate.getText());
        boolean matchesPaymentMethod = txtPaymentMethod.getText().isEmpty()
                || payment.getPaymentMethod().toLowerCase().contains(txtPaymentMethod.getText().toLowerCase());
        boolean matchesAmount = txtAmount.getText().isEmpty()
                || String.valueOf(payment.getAmount()).contains(txtAmount.getText());

        return matchesPaymentID && matchesOrderID && matchesPaymentDate && matchesPaymentMethod && matchesAmount;
    }

    private void showEditForm(Payment payment) {
        paymentEditForm.showEditForm(payment);
        if (!view.getChildren().contains(paymentEditForm.getForm())) {
            view.getChildren().add(paymentEditForm.getForm());
        }
    }

    private void showAddForm() {
        paymentAddForm.showAddForm();
        if (!view.getChildren().contains(paymentAddForm.getForm())) {
            view.getChildren().add(paymentAddForm.getForm());
        }
    }

   
    public void refreshData() {
        paymentsData = fetchPaymentData();
        paymentsTableView.setItems(paymentsData);
    }
    public void reloadScene() {
        // Get the parent layout (CarShopGUI's main content)
        VBox parent = (VBox) view.getParent();
        if (parent != null) {
            PaymentsSection refreshedPaymentsSection = new PaymentsSection();
            parent.getChildren().setAll(refreshedPaymentsSection.getView());
        }
    }
    public void hideEditForm() {
        view.getChildren().remove(paymentEditForm.getForm());
    }

    public void hideAddForm() {
        view.getChildren().remove(paymentAddForm.getForm());
    }

    public VBox getView() {
        return view;
    }
}
