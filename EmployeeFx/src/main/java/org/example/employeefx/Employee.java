package org.example.employeefx;

public class Employee {
    private String name;
    private String department;
    private double salary;
    private String status;  // New field for status

    // Constructor
    public Employee(String name, String department, double salary, String status) {
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.status = status;
    }

    public Employee(String name, String department, double salary) {
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + " - " + department + " - $" + salary + " - " + status;  // Include status in the display
    }
}
