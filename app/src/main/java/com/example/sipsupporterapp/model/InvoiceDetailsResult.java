package com.example.sipsupporterapp.model;

public class InvoiceDetailsResult {

    private String errorCode;
    private String error;
    private InvoiceDetailsInfo[] invoiceDetails;

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

    public InvoiceDetailsInfo[] getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(InvoiceDetailsInfo[] invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }
}
