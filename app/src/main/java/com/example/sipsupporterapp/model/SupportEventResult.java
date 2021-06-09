package com.example.sipsupporterapp.model;

public class SupportEventResult {

    private String error;
    private String errorCode;
    private SupportEventInfo[] supportEvents;

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

    public SupportEventInfo[] getSupportEvents() {
        return supportEvents;
    }

    public void setSupportEvents(SupportEventInfo[] supportEvents) {
        this.supportEvents = supportEvents;
    }
}
