package com.example;

public class Car {
   private int carID;
   private String make;
   private String model;
   private int year;
   private double price;
   private int stock;
   private String vin;

   // Constructor
   public Car(int carID, String make, String model, int year, double price, int stock, String vin) {
      this.carID = carID;
      this.make = make;
      this.model = model;
      this.year = year;
      this.price = price;
      this.stock = stock;
      this.vin = vin;
   }

   // Getters and setters
   public int getCarID() {
      return carID;
   }

   public void setCarID(int carID) {
      this.carID = carID;
   }

   public String getMake() {
      return make;
   }

   public void setMake(String make) {
      this.make = make;
   }

   public String getModel() {
      return model;
   }

   public void setModel(String model) {
      this.model = model;
   }

   public int getYear() {
      return year;
   }

   public void setYear(int year) {
      this.year = year;
   }

   public double getPrice() {
      return price;
   }

   public void setPrice(double price) {
      this.price = price;
   }

   public int getStock() {
      return stock;
   }

   public void setStock(int stock) {
      this.stock = stock;
   }

   public String getVin() {
      return vin;
   }

   public void setVin(String vin) {
      this.vin = vin;
   }
}
