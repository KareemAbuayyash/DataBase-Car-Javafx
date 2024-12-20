package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class CarSection {
    private VBox view;
    private TableView<Car> carTableView;
    private ObservableList<Car> carData;
    private CarEditForm carEditForm;
    private CarAddForm carAddForm;

    public CarSection() {
        // Initialize forms
        carEditForm = new CarEditForm(this);
        carAddForm = new CarAddForm(this);
        
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Create the TableView
        carTableView = new TableView<>();
        carTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        carTableView.setPrefSize(800, 400);

        // Define columns
        TableColumn<Car, Integer> colCarID = new TableColumn<>("Car ID");
        colCarID.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<Car, String> colMake = new TableColumn<>("Make");
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<Car, String> colModel = new TableColumn<>("Model");
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Car, Integer> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<Car, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Car, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Car, String> colVIN = new TableColumn<>("VIN");
        colVIN.setCellValueFactory(new PropertyValueFactory<>("vin"));

        // Add Edit Icon Column
        TableColumn<Car, Void> colEdit = createEditColumn();

        // Add columns to the TableView
        carTableView.getColumns().addAll(colCarID, colMake, colModel, colYear, colPrice, colStock, colVIN, colEdit);

        // Disable column reordering
        carTableView.getColumns().forEach(column -> column.setReorderable(false));

        // Load data from the database
        carData = fetchCarData();
        FilteredList<Car> filteredData = new FilteredList<>(carData, p -> true);
        carTableView.setItems(filteredData);

        // Create search fields
        TextField txtCarID =TextFieldFactory.createTextField("Car ID");

        TextField txtMakeSearch = TextFieldFactory.createTextField("Make");

        TextField txtModelSearch = TextFieldFactory.createTextField("Model");

        ComboBox<Integer> yearComboBox = new ComboBox<>();
        yearComboBox.setPromptText("Year");
        updateYearComboBox(yearComboBox);
        yearComboBox.setStyle("-fx-font-size: 12px; -fx-padding: 6px; -fx-background-radius: 10px;");

        TextField txtPriceSearch =TextFieldFactory.createTextField("Price");

        TextField txtStockSearch = TextFieldFactory.createTextField("Stock");

        TextField txtVINSearch = TextFieldFactory.createTextField("VIN");

        // Add filtering logic
        txtCarID.textProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        txtMakeSearch.textProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        txtModelSearch.textProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        txtPriceSearch.textProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        txtStockSearch.textProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        txtVINSearch.textProperty().addListener((observable, oldValue, newValue) -> 
            filteredData.setPredicate(car -> filterCars(car, txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch))
        );

        // Reset button to clear all text fields
        Button btnReset = ButtonFactory.createActionButton( "Reset");
        btnReset.setOnAction(e -> {
            txtCarID.clear();
            txtMakeSearch.clear();
            txtModelSearch.clear();
            yearComboBox.setValue(null);
            txtPriceSearch.clear();
            txtStockSearch.clear();
            txtVINSearch.clear();
        });

        

        // Layout for search fields with label and reset button
        Label lblSearch = new Label("Search:");
        HBox searchFields = new HBox(8,  txtCarID, txtMakeSearch, txtModelSearch, yearComboBox, txtPriceSearch, txtStockSearch, txtVINSearch);
        searchFields.setAlignment(Pos.CENTER_LEFT);
        searchFields.setPadding(new Insets(5));

        // Add Buttons for Adding Cars
        Button btnAddCar = ButtonFactory.createActionButton("Add Car");
        btnAddCar.setOnAction(e -> {
            hideEditForm(); // Hide edit form if visible
            showAddForm();
        });

        HBox hboxButtons = new HBox(10, btnAddCar, btnReset);
        hboxButtons.setAlignment(Pos.CENTER);
        hboxButtons.setPadding(new Insets(10));

        // Add everything to the main view
        view.getChildren().addAll(lblSearch,searchFields, hboxButtons, carTableView);
    }

    private void updateYearComboBox(ComboBox<Integer> yearComboBox) {
        ObservableList<Integer> years = FXCollections.observableArrayList(
                carData.stream().map(Car::getYear).distinct().sorted().collect(Collectors.toList())
        );
        yearComboBox.setItems(years);
    }

    private boolean filterCars(Car car, TextField txtCarID, TextField txtMakeSearch, TextField txtModelSearch,
    ComboBox<Integer> yearComboBox, TextField txtPriceSearch, TextField txtStockSearch, TextField txtVINSearch) {
        boolean matchesCarID = txtCarID.getText().isEmpty()
        || String.valueOf(car.getCarID()).equals(txtCarID.getText());
boolean matchesMake = txtMakeSearch.getText().isEmpty()
|| car.getMake().toLowerCase().contains(txtMakeSearch.getText().toLowerCase());
boolean matchesModel = txtModelSearch.getText().isEmpty()
|| car.getModel().toLowerCase().contains(txtModelSearch.getText().toLowerCase());
boolean matchesYear = yearComboBox.getValue() == null || car.getYear() == yearComboBox.getValue();
boolean matchesPrice = txtPriceSearch.getText().isEmpty()
|| String.valueOf(car.getPrice()).contains(txtPriceSearch.getText());
boolean matchesStock = txtStockSearch.getText().isEmpty()
|| String.valueOf(car.getStock()).equals(txtStockSearch.getText());
boolean matchesVIN = txtVINSearch.getText().isEmpty()
|| car.getVin().equals(txtVINSearch.getText()); // Exact match for VIN

return matchesCarID && matchesMake && matchesModel && matchesYear && matchesPrice && matchesStock && matchesVIN;
}

    private TableColumn<Car, Void> createEditColumn() {
        TableColumn<Car, Void> colEdit = new TableColumn<>("Edit");
        colEdit.setCellFactory(param -> new TableCell<Car, Void>() {
            private final Button editButton = ButtonFactory.createEditButton();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    Car car = getTableView().getItems().get(getIndex());
                    hideAddForm(); // Hide the Add Form when Edit is clicked
                    showEditForm(car);
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

    private ObservableList<Car> fetchCarData() {
        ObservableList<Car> carList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Cars";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("CarID"),
                        rs.getString("Make"),
                        rs.getString("Model"),
                        rs.getInt("Year"),
                        rs.getDouble("Price"),
                        rs.getInt("Stock"),
                        rs.getString("VIN")
                );
                carList.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load car data.");
        }
        return carList;
    }

    private void showEditForm(Car car) {
        carEditForm.showEditForm(car);
        if (!view.getChildren().contains(carEditForm.getForm())) {
            view.getChildren().add(carEditForm.getForm());
        }
    }

    private void showAddForm() {
        carAddForm.showAddForm();
        if (!view.getChildren().contains(carAddForm.getForm())) {
            view.getChildren().add(carAddForm.getForm());
        }
    }

    public void refreshData() {
        carData = fetchCarData();
        carTableView.setItems(carData);
        carTableView.refresh(); // Force refresh the TableView
    }
    

    public void hideEditForm() {
        view.getChildren().remove(carEditForm.getForm());
    }

    public void hideAddForm() {
        view.getChildren().remove(carAddForm.getForm());
    }
    public void reloadScene() {
        // Get the parent layout (CarShopGUI's main content)
        VBox parent = (VBox) view.getParent();
        if (parent != null) {
            CarSection refreshedCarSection = new CarSection();
            parent.getChildren().setAll(refreshedCarSection.getView());
        }
    }
    
    public VBox getView() {
        return view;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
