package com.example.sipsupporterapp.model;

public class ProductGroupResult {

    private String errorCode;
    private String error;
    private ProductGroupInfo[] productGroups;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ProductGroupInfo[] getProductGroups() {
        return productGroups;
    }

    public void setProductGroups(ProductGroupInfo[] productGroups) {
        this.productGroups = productGroups;
    }
}
