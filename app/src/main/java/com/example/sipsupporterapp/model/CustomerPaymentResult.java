package com.example.sipsupporterapp.model;

public class CustomerPaymentResult {

    private String error;
    private String errorCode;
    private CustomerPaymentInfo[] customerPayments;

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

    public CustomerPaymentInfo[] getCustomerPayments() {
        return customerPayments;
    }

    public void setCustomerPayments(CustomerPaymentInfo[] customerPayments) {
        this.customerPayments = customerPayments;
    }
    public class CustomerPaymentInfo {

        private int customerPaymentID;
        private int customerID;
        private int bankAccountID;
        private int datePayment;
        private int userID;
        private int managerOkUserID;
        private int attachCount;
        private long addTime;
        private long price;
        private long managerOkTime;
        private boolean managerOk;
        private String customerName;
        private String bankAccountNO;
        private String bankName;
        private String bankAccountName;
        private String description;
        private String userFullName;
        private String managerOkUserFullName;

        public int getCustomerPaymentID() {
            return customerPaymentID;
        }

        public void setCustomerPaymentID(int customerPaymentID) {
            this.customerPaymentID = customerPaymentID;
        }

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public int getBankAccountID() {
            return bankAccountID;
        }

        public void setBankAccountID(int bankAccountID) {
            this.bankAccountID = bankAccountID;
        }

        public int getDatePayment() {
            return datePayment;
        }

        public void setDatePayment(int datePayment) {
            this.datePayment = datePayment;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getManagerOkUserID() {
            return managerOkUserID;
        }

        public void setManagerOkUserID(int managerOkUserID) {
            this.managerOkUserID = managerOkUserID;
        }

        public int getAttachCount() {
            return attachCount;
        }

        public void setAttachCount(int attachCount) {
            this.attachCount = attachCount;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getManagerOkTime() {
            return managerOkTime;
        }

        public void setManagerOkTime(long managerOkTime) {
            this.managerOkTime = managerOkTime;
        }

        public boolean isManagerOk() {
            return managerOk;
        }

        public void setManagerOk(boolean managerOk) {
            this.managerOk = managerOk;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getBankAccountNO() {
            return bankAccountNO;
        }

        public void setBankAccountNO(String bankAccountNO) {
            this.bankAccountNO = bankAccountNO;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankAccountName() {
            return bankAccountName;
        }

        public void setBankAccountName(String bankAccountName) {
            this.bankAccountName = bankAccountName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public String getManagerOkUserFullName() {
            return managerOkUserFullName;
        }

        public void setManagerOkUserFullName(String managerOkUserFullName) {
            this.managerOkUserFullName = managerOkUserFullName;
        }
    }
}
