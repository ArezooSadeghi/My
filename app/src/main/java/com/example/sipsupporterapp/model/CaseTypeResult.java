package com.example.sipsupporterapp.model;

public class CaseTypeResult {

    private String error;
    private String errorCode;
    private CaseTypeInfo[] caseTypes;

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

    public CaseTypeInfo[] getCaseTypes() {
        return caseTypes;
    }

    public void setCaseTypes(CaseTypeInfo[] caseTypes) {
        this.caseTypes = caseTypes;
    }

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
}
