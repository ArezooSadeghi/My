package com.example.sipsupporterapp.model;

public class InvoiceResult {

    private String errorCode;
    private String error;
    private InvoiceInfo[] invoices;

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

    public InvoiceInfo[] getInvoices() {
        return invoices;
    }

    public void setInvoices(InvoiceInfo[] invoices) {
        this.invoices = invoices;
    }
}
