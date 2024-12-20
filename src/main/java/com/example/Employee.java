package com.example;

import java.sql.Date;
import java.time.LocalDate;

public class Employee {
    private int employeeID;
    private String firstName;
    private String lastName;
    private String position;
    private double salary;
    private Date hireDate;

    // Constructor
    public Employee(int employeeID, String firstName, String lastName, String position, double salary, Date hireDate) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    // Getters and Setters
    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
       this.firstName = firstName;
    }

    public String getLastName() {
       return lastName;
    }

    public void setLastName(String lastName) {
       this.lastName = lastName;
    }

    public String getPosition() {
       return position;
    }

    public void setPosition(String position) {
       this.position = position;
    }

    public double getSalary() {
       return salary;
    }

    public void setSalary(double salary) {
       this.salary = salary;
    }

    public Date getHireDate() {
       return hireDate;
    }

    public void setHireDate(Date hireDate) {
      this.hireDate = hireDate;
    }
}
