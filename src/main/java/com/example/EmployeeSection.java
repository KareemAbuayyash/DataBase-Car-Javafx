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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeSection {
    private TableView<Employee> employeeTableView;
    private VBox view;
    private EmployeeEditForm employeeEditForm;
    private EmployeeAddForm employeeAddForm;
    private ObservableList<Employee> employeeData;

    public EmployeeSection() {
        // Initialize forms
        employeeEditForm = new EmployeeEditForm(this);
        employeeAddForm = new EmployeeAddForm(this);
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Create the TableView
        employeeTableView = new TableView<>();
        employeeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        employeeTableView.setPrefSize(750, 400);

        // Define columns
        TableColumn<Employee, Integer> colEmployeeID = new TableColumn<>("Employee ID");
        colEmployeeID.setCellValueFactory(new PropertyValueFactory<>("employeeID"));

        TableColumn<Employee, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, String> colPosition = new TableColumn<>("Position");
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<Employee, Double> colSalary = new TableColumn<>("Salary");
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, Date> colHireDate = new TableColumn<>("Hire Date");
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));

        // Add Edit Icon Column
        TableColumn<Employee, Void> colEdit = createEditColumn();

        // Add columns to the TableView
        employeeTableView.getColumns().addAll(colEmployeeID, colFirstName, colLastName, colPosition, colSalary, colHireDate, colEdit);
        employeeTableView.getColumns().forEach(column -> column.setReorderable(false));


        // Load data from the database
        employeeData = fetchEmployeeData();
        FilteredList<Employee> filteredData = new FilteredList<>(employeeData, p -> true);
        employeeTableView.setItems(filteredData);

        // Create search fields
        TextField txtEmployeeID = new TextField();
        txtEmployeeID.setPromptText("Employee ID");

        TextField txtFirstName = new TextField();
        txtFirstName.setPromptText("First Name");

        TextField txtLastName = new TextField();
        txtLastName.setPromptText("Last Name");

        TextField txtPosition = new TextField();
        txtPosition.setPromptText("Position");

        TextField txtSalary = new TextField();
        txtSalary.setPromptText("Salary");

        TextField txtHireDate = new TextField();
        txtHireDate.setPromptText("Hire Date");

        // Add filtering logic
        txtEmployeeID.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                employee -> filterEmployees(employee, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate)));

        txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                employee -> filterEmployees(employee, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate)));

        txtLastName.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                employee -> filterEmployees(employee, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate)));

        txtPosition.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                employee -> filterEmployees(employee, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate)));

        txtSalary.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                employee -> filterEmployees(employee, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate)));

        txtHireDate.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(
                employee -> filterEmployees(employee, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate)));

        // Reset button to clear all text fields
        Button btnReset = ButtonFactory.createActionButton("Reset");
        btnReset.setOnAction(e -> {
            txtEmployeeID.clear();
            txtFirstName.clear();
            txtLastName.clear();
            txtPosition.clear();
            txtSalary.clear();
            txtHireDate.clear();
        });

        

        // Layout for search fields with label and reset button
        Label lblSearch = new Label("Search:");
        HBox searchFields = new HBox(15, lblSearch, txtEmployeeID, txtFirstName, txtLastName, txtPosition, txtSalary, txtHireDate);
        searchFields.setAlignment(Pos.CENTER_LEFT);
        searchFields.setPadding(new Insets(5));

        // Add Buttons for Adding Employees
        Button btnAddEmployee = ButtonFactory.createActionButton("Add Employee");
        btnAddEmployee.setOnAction(e -> {
            hideEditForm(); // Hide edit form if visible
            showAddForm();
        });

        HBox hboxButtons = new HBox(10, btnAddEmployee, btnReset);
        hboxButtons.setAlignment(Pos.CENTER);
        hboxButtons.setPadding(new Insets(10));

        // Add everything to the main view
        view.getChildren().addAll(lblSearch, searchFields, hboxButtons, employeeTableView);
    }

    private TableColumn<Employee, Void> createEditColumn() {
        TableColumn<Employee, Void> colEdit = new TableColumn<>("Edit");
        colEdit.setCellFactory(param -> new TableCell<Employee, Void>() {
            private final Button editButton = ButtonFactory.createEditButton();

            {
                FontIcon editIcon = new FontIcon("fas-edit");
                editIcon.setIconSize(16);
                editButton.setGraphic(editIcon);
                editButton.setOnAction(event -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    hideAddForm(); // Hide the Add Form when Edit is clicked
                    showEditForm(employee);
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

   

    private ObservableList<Employee> fetchEmployeeData() {
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Employees")) {

            while (rs.next()) {
                employeeList.add(new Employee(
                        rs.getInt("EmployeeID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Position"),
                        rs.getDouble("Salary"),
                        rs.getDate("HireDate")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    private boolean filterEmployees(Employee employee, TextField txtEmployeeID, TextField txtFirstName, TextField txtLastName,
                                    TextField txtPosition, TextField txtSalary, TextField txtHireDate) {
        boolean matchesEmployeeID = txtEmployeeID.getText().isEmpty()
                || String.valueOf(employee.getEmployeeID()).equals(txtEmployeeID.getText());
        boolean matchesFirstName = txtFirstName.getText().isEmpty()
                || employee.getFirstName().toLowerCase().contains(txtFirstName.getText().toLowerCase());
        boolean matchesLastName = txtLastName.getText().isEmpty()
                || employee.getLastName().toLowerCase().contains(txtLastName.getText().toLowerCase());
        boolean matchesPosition = txtPosition.getText().isEmpty()
                || employee.getPosition().toLowerCase().contains(txtPosition.getText().toLowerCase());
        boolean matchesSalary = txtSalary.getText().isEmpty()
                || String.valueOf(employee.getSalary()).contains(txtSalary.getText());
        boolean matchesHireDate = txtHireDate.getText().isEmpty()
                || employee.getHireDate().toString().toLowerCase().contains(txtHireDate.getText().toLowerCase());

        return matchesEmployeeID && matchesFirstName && matchesLastName && matchesPosition && matchesSalary && matchesHireDate;
    }

    private void showEditForm(Employee employee) {
        employeeEditForm.showEditForm(employee);
        if (!view.getChildren().contains(employeeEditForm.getForm())) {
            view.getChildren().add(employeeEditForm.getForm());
        }
    }

    private void showAddForm() {
        employeeAddForm.showAddForm();
        if (!view.getChildren().contains(employeeAddForm.getForm())) {
            view.getChildren().add(employeeAddForm.getForm());
        }
    }

   
    public void refreshData() {
        employeeData = fetchEmployeeData();
        employeeTableView.setItems(employeeData);
    }
    public void reloadScene() {
        // Get the parent layout (CarShopGUI's main content)
        VBox parent = (VBox) view.getParent();
        if (parent != null) {
            EmployeeSection refreshedEmplyeeSection = new EmployeeSection();
            parent.getChildren().setAll(refreshedEmplyeeSection.getView());
        }
    }
    public void hideEditForm() {
        view.getChildren().remove(employeeEditForm.getForm());
    }

    public void hideAddForm() {
        view.getChildren().remove(employeeAddForm.getForm());
    }

    public VBox getView() {
        return view;
    }
}
