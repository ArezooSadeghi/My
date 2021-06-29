package com.example.sipsupporterapp.model;

public class CaseTypeInfo {

    private int caseTypeID;
    private int userID;
    private long addTime;
    private boolean notRequiredCustomer;
    private String caseType;
    private String userFullName;

    public int getCaseTypeID() {
        return caseTypeID;
    }

    public void setCaseTypeID(int caseTypeID) {
        this.caseTypeID = caseTypeID;
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

    public boolean isNotRequiredCustomer() {
        return notRequiredCustomer;
    }

    public void setNotRequiredCustomer(boolean notRequiredCustomer) {
        this.notRequiredCustomer = notRequiredCustomer;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
