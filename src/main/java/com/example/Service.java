package com.example;

import java.time.LocalDate;

public class Service {
    private int serviceID;
    private int carID;
    private int customerID;
    private LocalDate serviceDate;
    private String serviceDescription;
    private double cost;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    private String carMake;
    private String carModel;
    private int carYear;
    private double carPrice;
    private int carStock;
    private String vin;

    // Full Constructor
    public Service(int serviceID, int carID, int customerID, LocalDate serviceDate, String serviceDescription, double cost,
                   String firstName, String lastName, String email, String phone, String address, String city, String state, String zipCode,
                   String carMake, String carModel, int carYear, double carPrice, int carStock, String vin) {
        this.serviceID = serviceID;
        this.carID = carID;
        this.customerID = customerID;
        this.serviceDate = serviceDate;
        this.serviceDescription = serviceDescription;
        this.cost = cost;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.carMake = carMake;
        this.carModel = carModel;
        this.carYear = carYear;
        this.carPrice = carPrice;
        this.carStock = carStock;
        this.vin = vin;
    }

    // Constructor for basic service fields
    public Service(int serviceID, int carID, int customerID, LocalDate serviceDate, String serviceDescription, double cost) {
        this.serviceID = serviceID;
        this.carID = carID;
        this.customerID = customerID;
        this.serviceDate = serviceDate;
        this.serviceDescription = serviceDescription;
        this.cost = cost;
    }

    // Getters and Setters for basic service fields
    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    // Getters and Setters for additional customer details
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

    // Getters and Setters for additional car details
    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getCarYear() {
        return carYear;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }

    public double getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(double carPrice) {
        this.carPrice = carPrice;
    }

    public int getCarStock() {
        return carStock;
    }

    public void setCarStock(int carStock) {
        this.carStock = carStock;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceID=" + serviceID +
                ", carID=" + carID +
                ", customerID=" + customerID +
                ", serviceDate=" + serviceDate +
                ", serviceDescription='" + serviceDescription + '\'' +
                ", cost=" + cost +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", carMake='" + carMake + '\'' +
                ", carModel='" + carModel + '\'' +
                ", carYear=" + carYear +
                ", carPrice=" + carPrice +
                ", carStock=" + carStock +
                ", vin='" + vin + '\'' +
                '}';
    }
}
