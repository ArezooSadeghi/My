package com.example.sipsupporterapp.model;

public class PaymentResult {

    private String error;
    private String errorCode;
    private PaymentInfo[] payments;

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

    public PaymentInfo[] getPayments() {
        return payments;
    }

    public void setPayments(PaymentInfo[] payments) {
        this.payments = payments;
    }

    public class PaymentInfo {

        private int paymentID;
        private int bankAccountID;
        private int paymentSubjectID;
        private int datePayment;
        private int userID;
        private int attachCount;
        private long price;
        private long addTime;
        private boolean ManagerOK;
        private String bankAccountName;
        private String paymentSubject;
        private String parentPaymentSubject;
        private String description;
        private String userFullName;

        public int getPaymentID() {
            return paymentID;
        }

        public void setPaymentID(int paymentID) {
            this.paymentID = paymentID;
        }

        public int getBankAccountID() {
            return bankAccountID;
        }

        public void setBankAccountID(int bankAccountID) {
            this.bankAccountID = bankAccountID;
        }

        public int getPaymentSubjectID() {
            return paymentSubjectID;
        }

        public void setPaymentSubjectID(int paymentSubjectID) {
            this.paymentSubjectID = paymentSubjectID;
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

        public int getAttachCount() {
            return attachCount;
        }

        public void setAttachCount(int attachCount) {
            this.attachCount = attachCount;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public boolean isManagerOK() {
            return ManagerOK;
        }

        public void setManagerOK(boolean managerOK) {
            ManagerOK = managerOK;
        }

        public String getBankAccountName() {
            return bankAccountName;
        }

        public void setBankAccountName(String bankAccountName) {
            this.bankAccountName = bankAccountName;
        }

        public String getPaymentSubject() {
            return paymentSubject;
        }

        public void setPaymentSubject(String paymentSubject) {
            this.paymentSubject = paymentSubject;
        }

        public String getParentPaymentSubject() {
            return parentPaymentSubject;
        }

        public void setParentPaymentSubject(String parentPaymentSubject) {
            this.parentPaymentSubject = parentPaymentSubject;
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
    }
}
