package com.example.sipsupporterapp.eventbus;

public class PostCustomerIDEvent {
    private int customerID;
    private String customerName;

    public PostCustomerIDEvent(int customerID, String customerName) {
        this.customerID = customerID;
        this.customerName = customerName;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
