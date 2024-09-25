package org.example.employeefx;

public class Manager extends Employee {  // Extending Employee class
    private double bonus;

    // Constructor for Manager class
    public Manager(String name, String department, double salary, double bonus) {
        super(name, department, salary);  // Call the constructor of the Employee class
        this.bonus = bonus;  // Manager-specific attribute
    }

    // Getter for bonus
    public double getBonus() {
        return bonus;
    }

    // Setter for bonus
    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    // Overriding the toString() method to include bonus information
    @Override
    public String toString() {
        return super.toString() + " - Bonus: $" + bonus;  // Use the Employee's toString() and add bonus info
    }
}
