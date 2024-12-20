package com.example;

import java.sql.Date;

public class Order {
    private int orderID;
    private Date orderDate;
    private int carID;
    private int customerID;
    private int employeeID;
    private int quantity;
    private double totalPrice;

    // Constructor
    public Order(int orderID, Date orderDate, int carID, int customerID, int employeeID, int quantity, double totalPrice) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.carID = carID;
        this.customerID = customerID;
        this.employeeID = employeeID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Order(int int1, Date date, int int2, int string, int string2, String int3, String int4, int int5,
            double double1) {
        //TODO Auto-generated constructor stub
    }

    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public int getCarID() {
        return carID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Setters
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
