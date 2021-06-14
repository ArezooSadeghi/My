package com.example.sipsupporterapp.eventbus;

public class PostProductGroupIDEvent {

    private int productGroupID;
    private String productGroup;

    public PostProductGroupIDEvent(int productGroupID, String productGroup) {
        this.productGroupID = productGroupID;
        this.productGroup = productGroup;
    }

    public int getProductGroupID() {
        return productGroupID;
    }

    public void setProductGroupID(int productGroupID) {
        this.productGroupID = productGroupID;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }
}
