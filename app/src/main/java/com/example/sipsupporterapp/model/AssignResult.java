package com.example.sipsupporterapp.model;

public class AssignResult {

    private String errorCode;
    private String error;
    private AssignInfo[] assigns;

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

    public AssignInfo[] getAssigns() {
        return assigns;
    }

    public void setAssigns(AssignInfo[] assigns) {
        this.assigns = assigns;
    }
}
