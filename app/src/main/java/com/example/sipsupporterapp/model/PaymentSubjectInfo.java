package com.example.sipsupporterapp.model;

public class PaymentSubjectInfo {

    private int paymentSubjectID;
    private int row;
    private int parentID;
    private int userID;
    private long addTime;
    private String paymentSubject;
    private String parentPaymentSubject;
    private String userFullName;

    public int getPaymentSubjectID() {
        return paymentSubjectID;
    }

    public void setPaymentSubjectID(int paymentSubjectID) {
        this.paymentSubjectID = paymentSubjectID;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
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

    public String getPaymentSubject() {
        return paymentSubject;
    }

    public void setPaymentSubject(String paymentSubject) {
        this.paymentSubject = paymentSubject;
    }

    public String getParentPaymentSubject() {
        return parentPaymentSubject;
    }

    public void setParentPaymentSubject(String parentPaymentSubject) {
        this.parentPaymentSubject = parentPaymentSubject;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
