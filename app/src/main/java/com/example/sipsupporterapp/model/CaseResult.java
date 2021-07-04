package com.example.sipsupporterapp.model;

public class CaseResult {

    private String errorCode;
    private String error;
    private CaseInfo[] cases;

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

    public CaseInfo[] getCases() {
        return cases;
    }

    public void setCases(CaseInfo[] cases) {
        this.cases = cases;
    }
}
