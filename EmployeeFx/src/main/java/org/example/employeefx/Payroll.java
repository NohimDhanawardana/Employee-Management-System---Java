package org.example.employeefx;

    public class Payroll {
        // Calculate salary for a regular employee
        public static double calculateEmployeeSalary(Employee emp) {
            return emp.getSalary();  // No bonuses for regular employees
        }

        // Calculate salary for a manager (including bonus)
        public static double calculateManagerSalary(Manager manager) {
            return manager.getSalary() + manager.getBonus();  // Manager gets base salary + bonus
        }
    }
