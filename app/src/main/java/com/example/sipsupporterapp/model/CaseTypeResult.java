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
}
