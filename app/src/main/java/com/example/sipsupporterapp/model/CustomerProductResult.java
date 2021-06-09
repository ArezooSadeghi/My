package com.example.sipsupporterapp.model;

public class CustomerProductResult {

    private String error;
    private String errorCode;
    private CustomerProductInfo[] customerProducts;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public CustomerProductInfo[] getCustomerProducts() {
        return customerProducts;
    }

    public void setCustomerProducts(CustomerProductInfo[] customerProducts) {
        this.customerProducts = customerProducts;
    }
}
