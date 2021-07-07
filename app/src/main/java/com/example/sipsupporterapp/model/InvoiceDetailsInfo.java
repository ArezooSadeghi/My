package com.example.sipsupporterapp.model;

public class InvoiceDetailsInfo {

    private int invoiceDetailsID;
    private int invoiceID;
    private int productID;
    private int QTY;
    private int unitPrice;
    private int sumDiscountPrice;
    private int userID;
    private long addTime;
    private String userFullName;
    private String productName;
    private String description;

    public int getInvoiceDetailsID() {
        return invoiceDetailsID;
    }

    public void setInvoiceDetailsID(int invoiceDetailsID) {
        this.invoiceDetailsID = invoiceDetailsID;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQTY() {
        return QTY;
    }

    public void setQTY(int QTY) {
        this.QTY = QTY;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSumDiscountPrice() {
        return sumDiscountPrice;
    }

    public void setSumDiscountPrice(int sumDiscountPrice) {
        this.sumDiscountPrice = sumDiscountPrice;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
