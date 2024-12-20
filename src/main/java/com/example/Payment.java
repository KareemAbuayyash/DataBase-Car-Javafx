package com.example;

import java.time.LocalDate;

public class Payment {
    private int paymentID;
    private int orderID;
    private LocalDate paymentDate;
    private String paymentMethod;
    private double amount;
    private String firstName; // Added for ReportCView
    private String lastName; // Added for ReportCView

    // Constructor
    public Payment(int paymentID, int orderID, LocalDate paymentDate, String paymentMethod, double amount) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public Payment(int paymentID, int orderID, LocalDate paymentDate, String paymentMethod, double amount,
            String firstName, String lastName) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
}
