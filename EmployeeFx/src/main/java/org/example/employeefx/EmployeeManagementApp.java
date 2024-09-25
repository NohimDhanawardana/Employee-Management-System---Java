package org.example.employeefx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeManagementApp extends Application {
    private ArrayList<Employee> employees = new ArrayList<>();
    private ObservableList<String> employeeObservableList = FXCollections.observableArrayList();
    private ListView<String> employeeListView = new ListView<>(employeeObservableList);
    private TextField nameField = new TextField();
    private TextField departmentField = new TextField();
    private TextField salaryField = new TextField();
    private ComboBox<String> statusComboBox = new ComboBox<>();
    private TextField searchField = new TextField();
    private Employee selectedEmployee;
    private int selectedEmployeeIndex;

    // Labels for statistics dashboard
    private Label totalEmployeesLabel = new Label("Total Employees: 0");
    private Label averageSalaryLabel = new Label("Average Salary: 0.0");
    private Label departmentCountLabel = new Label("Employees per Department: None");

    // PieChart for employee status
    private PieChart statusPieChart = new PieChart();

    // BarChart for employees per department
    private BarChart<String, Number> departmentBarChart;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management System");

        // UI elements for adding/editing employees
        Label nameLabel = new Label("Name:");
        Label departmentLabel = new Label("Department:");
        Label salaryLabel = new Label("Salary:");
        Label statusLabel = new Label("Status:");

        // Status ComboBox
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Remote", "Hybrid"));

        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> addEmployee());

        Button editButton = new Button("Edit Employee");
        editButton.setOnAction(e -> editEmployee());

        Button deleteButton = new Button("Delete Employee");
        deleteButton.setOnAction(e -> deleteEmployee());

        Button sortByNameButton = new Button("Sort by Name");
        sortByNameButton.setOnAction(e -> sortByName());

        Button sortBySalaryButton = new Button("Sort by Salary");
        sortBySalaryButton.setOnAction(e -> sortBySalary());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveEmployeesToFile());

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> loadEmployeesFromFile());

        // Search field
        searchField.setPromptText("Search by name...");
        searchField.textProperty().addListener((obs, oldText, newText) -> filterEmployees(newText));

        // List view to display employees
        employeeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectEmployee(newValue));
        employeeListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                showEmployeeDetails();
            }
        });

        // Layout for the statistics section
        VBox statisticsBox = new VBox(10, totalEmployeesLabel, averageSalaryLabel, departmentCountLabel);

        // Create BarChart for employees per department
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Department");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Employees");

        departmentBarChart = new BarChart<>(xAxis, yAxis);
        departmentBarChart.setTitle("Employees per Department");

        // Main layout
        VBox vbox = new VBox(10, nameLabel, nameField, departmentLabel, departmentField, salaryLabel, salaryField,
                statusLabel, statusComboBox, addButton, editButton, deleteButton, sortByNameButton, sortBySalaryButton,
                saveButton, loadButton, searchField, employeeListView, statisticsBox, statusPieChart, departmentBarChart);

        // Wrap the VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true); // Ensures content width fits the viewport width.

        // Set the scene with the ScrollPane as the root
        Scene scene = new Scene(scrollPane, 1000, 1000);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load the initial statistics
        updateStatistics();
    }

    // Method to add new employees with validation
    private void addEmployee() {
        if (!validateInputs()) return;

        String name = nameField.getText();
        String department = departmentField.getText();
        double salary = Double.parseDouble(salaryField.getText());
        String status = statusComboBox.getValue();

        Employee employee = new Employee(name, department, salary, status);
        employees.add(employee);

        employeeObservableList.add(employee.toString());
        clearFields();

        // Update statistics after adding an employee
        updateStatistics();
    }

    // Method to edit an existing employee's details
    private void editEmployee() {
        if (selectedEmployee == null) {
            System.out.println("No employee selected.");
            return;
        }

        if (!validateInputs()) return;

        selectedEmployee.setName(nameField.getText());
        selectedEmployee.setDepartment(departmentField.getText());
        selectedEmployee.setSalary(Double.parseDouble(salaryField.getText()));
        selectedEmployee.setStatus(statusComboBox.getValue());

        employeeObservableList.set(selectedEmployeeIndex, selectedEmployee.toString());
        clearFields();

        // Update statistics after editing an employee
        updateStatistics();
    }

    // Method to delete an employee
    private void deleteEmployee() {
        if (selectedEmployee == null) {
            System.out.println("No employee selected.");
            return;
        }

        employees.remove(selectedEmployee);
        employeeObservableList.remove(selectedEmployeeIndex);
        clearFields();

        // Update statistics after deleting an employee
        updateStatistics();
    }

    // Method to select an employee from the list
    private void selectEmployee(String selectedEmployeeString) {
        if (selectedEmployeeString == null) return;

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).toString().equals(selectedEmployeeString)) {
                selectedEmployee = employees.get(i);
                selectedEmployeeIndex = i;

                nameField.setText(selectedEmployee.getName());
                departmentField.setText(selectedEmployee.getDepartment());
                salaryField.setText(String.valueOf(selectedEmployee.getSalary()));
                statusComboBox.setValue(selectedEmployee.getStatus());
                break;
            }
        }
    }

    // Sorting employees by name
    private void sortByName() {
        employees.sort(Comparator.comparing(Employee::getName));
        updateListView();
    }

    // Sorting employees by salary
    private void sortBySalary() {
        employees.sort(Comparator.comparingDouble(Employee::getSalary));
        updateListView();
    }

    // Filter employees by name (search)
    private void filterEmployees(String query) {
        employeeObservableList.clear();
        for (Employee emp : employees) {
            if (emp.getName().toLowerCase().contains(query.toLowerCase())) {
                employeeObservableList.add(emp.toString());
            }
        }
    }

    // Method to display more details about an employee (e.g., popup)
    private void showEmployeeDetails() {
        if (selectedEmployee != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Employee Details");
            alert.setHeaderText("Details of " + selectedEmployee.getName());
            alert.setContentText("Name: " + selectedEmployee.getName() +
                    "\nDepartment: " + selectedEmployee.getDepartment() +
                    "\nSalary: " + selectedEmployee.getSalary() +
                    "\nStatus: " + selectedEmployee.getStatus());
            alert.showAndWait();
        }
    }

    // Method to save employee data to a file
    private void saveEmployeesToFile() {
        try (FileWriter writer = new FileWriter("employees.txt")) {
            for (Employee emp : employees) {
                writer.write(emp.getName() + "," + emp.getDepartment() + "," + emp.getSalary() + "," + emp.getStatus() + "\n");
            }
            System.out.println("Employees saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load employee data from a file
    private void loadEmployeesFromFile() {
        employees.clear();
        employeeObservableList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String department = data[1];
                double salary = Double.parseDouble(data[2]);
                String status = data[3];
                Employee employee = new Employee(name, department, salary, status);
                employees.add(employee);
                employeeObservableList.add(employee.toString());
            }
            System.out.println("Employees loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update statistics after loading employees
        updateStatistics();
    }

    // Update statistics for the dashboard
    private void updateStatistics() {
        totalEmployeesLabel.setText("Total Employees: " + employees.size());
        if (!employees.isEmpty()) {
            double averageSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
            averageSalaryLabel.setText("Average Salary: " + String.format("%.2f", averageSalary));

            Map<String, Long> departmentCount = employees.stream()
                    .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
            departmentCountLabel.setText("Employees per Department: " + departmentCount);

            updatePieChart();
            updateBarChart(departmentCount);
        }
    }

    // Update the PieChart for employee status
    private void updatePieChart() {
        Map<String, Long> statusCount = employees.stream()
                .collect(Collectors.groupingBy(Employee::getStatus, Collectors.counting()));
        statusPieChart.getData().clear();
        statusCount.forEach((status, count) -> {
            PieChart.Data slice = new PieChart.Data(status, count);
            statusPieChart.getData().add(slice);
        });
    }

    // Update the BarChart for employees per department
    private void updateBarChart(Map<String, Long> departmentCount) {
        departmentBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        departmentCount.forEach((department, count) -> {
            series.getData().add(new XYChart.Data<>(department, count));
        });
        departmentBarChart.getData().add(series);
    }

    // Helper method to clear input fields
    private void clearFields() {
        nameField.clear();
        departmentField.clear();
        salaryField.clear();
        statusComboBox.setValue(null);
    }

    // Helper method to validate inputs
    private boolean validateInputs() {
        if (nameField.getText().isEmpty() || departmentField.getText().isEmpty() || salaryField.getText().isEmpty() ||
                statusComboBox.getValue() == null) {
            System.out.println("All fields must be filled.");
            return false;
        }

        try {
            Double.parseDouble(salaryField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Salary must be a number.");
            return false;
        }

        return true;
    }

    // Helper method to update the employee list view
    private void updateListView() {
        employeeObservableList.clear();
        for (Employee emp : employees) {
            employeeObservableList.add(emp.toString());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
