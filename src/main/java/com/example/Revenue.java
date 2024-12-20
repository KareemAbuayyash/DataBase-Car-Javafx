package com.example;
public class Revenue {
    private String date;  // Full date for Monthly, Quarter for Quarterly
    private String serviceType;
    private double revenue;

    public Revenue(String date, String serviceType, double revenue) {
        this.date = date;
        this.serviceType = serviceType;
        this.revenue = revenue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "Revenue{" +
                "date='" + date + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", revenue=" + revenue +
                '}';
    }
}
