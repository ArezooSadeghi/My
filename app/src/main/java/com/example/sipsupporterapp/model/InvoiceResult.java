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

    public class InvoiceInfo {

        private int invoiceID;
        private int customerID;
        private int caseID;
        private int SumPayment;
        private int sumPrice;
        private int sumDiscountPrice;
        private int VATprice;
        private int userID;
        private int closeUserID;
        private long addTime;
        private long closeTime;
        private boolean VAT;
        private String userFullName;
        private String invoiceDate;
        private String description;
        private String closeUserFullName;
        private String VAT_InvoiceNumber;
        private String VAT_CustomerName;
        private String VAT_EconomicCode;
        private String finalPriceHorof;
        private String customerName;

        public int getInvoiceID() {
            return invoiceID;
        }

        public void setInvoiceID(int invoiceID) {
            this.invoiceID = invoiceID;
        }

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public int getCaseID() {
            return caseID;
        }

        public void setCaseID(int caseID) {
            this.caseID = caseID;
        }

        public int getSumPayment() {
            return SumPayment;
        }

        public void setSumPayment(int sumPayment) {
            SumPayment = sumPayment;
        }

        public int getSumPrice() {
            return sumPrice;
        }

        public void setSumPrice(int sumPrice) {
            this.sumPrice = sumPrice;
        }

        public int getSumDiscountPrice() {
            return sumDiscountPrice;
        }

        public void setSumDiscountPrice(int sumDiscountPrice) {
            this.sumDiscountPrice = sumDiscountPrice;
        }

        public int getVATprice() {
            return VATprice;
        }

        public void setVATprice(int VATprice) {
            this.VATprice = VATprice;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getCloseUserID() {
            return closeUserID;
        }

        public void setCloseUserID(int closeUserID) {
            this.closeUserID = closeUserID;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public long getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(long closeTime) {
            this.closeTime = closeTime;
        }

        public boolean isVAT() {
            return VAT;
        }

        public void setVAT(boolean VAT) {
            this.VAT = VAT;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCloseUserFullName() {
            return closeUserFullName;
        }

        public void setCloseUserFullName(String closeUserFullName) {
            this.closeUserFullName = closeUserFullName;
        }

        public String getVAT_InvoiceNumber() {
            return VAT_InvoiceNumber;
        }

        public void setVAT_InvoiceNumber(String VAT_InvoiceNumber) {
            this.VAT_InvoiceNumber = VAT_InvoiceNumber;
        }

        public String getVAT_CustomerName() {
            return VAT_CustomerName;
        }

        public void setVAT_CustomerName(String VAT_CustomerName) {
            this.VAT_CustomerName = VAT_CustomerName;
        }

        public String getVAT_EconomicCode() {
            return VAT_EconomicCode;
        }

        public void setVAT_EconomicCode(String VAT_EconomicCode) {
            this.VAT_EconomicCode = VAT_EconomicCode;
        }

        public String getFinalPriceHorof() {
            return finalPriceHorof;
        }

        public void setFinalPriceHorof(String finalPriceHorof) {
            this.finalPriceHorof = finalPriceHorof;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
    }
}
