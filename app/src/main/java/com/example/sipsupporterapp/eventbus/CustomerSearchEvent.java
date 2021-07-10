package com.example.sipsupporterapp.eventbus;

public class CustomerSearchEvent {

    private int customerID;

    public CustomerSearchEvent(int customerID) {
        this.customerID = customerID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
}
