package com.example;

public class Customer {
    private int customerID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    // Constructor
    public Customer(int customerID, String firstName, String lastName, String email, String phone,
                    String address, String city, String state, String zipCode) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Getters and Setters
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
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

    public String getEmail() {
       return email;
    }

    public void setEmail(String email) {
       this.email = email;
    }

    public String getPhone() {
       return phone;
    }

    public void setPhone(String phone) {
       this.phone = phone;
    }

    public String getAddress() {
       return address;
    }

    public void setAddress(String address) {
       this.address = address;
    }

    public String getCity() {
       return city;
    }

    public void setCity(String city) {
       this.city = city;
    }

    public String getState() {
       return state;
    }

    public void setState(String state) {
       this.state = state;
    }

    public String getZipCode() {
       return zipCode;
    }

    public void setZipCode(String zipCode) {
       this.zipCode = zipCode;
    }
}
