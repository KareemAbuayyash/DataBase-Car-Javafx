package com.example;

public class MonthlyService {
    private String date;
    private String serviceDescription;
    private int serviceCount;

    public MonthlyService(String date, String serviceDescription, int serviceCount) {
        this.date = date;
        this.serviceDescription = serviceDescription;
        this.serviceCount = serviceCount;
    }

    public String getDate() {
        return date;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public int getServiceCount() {
        return serviceCount;
    }
}
