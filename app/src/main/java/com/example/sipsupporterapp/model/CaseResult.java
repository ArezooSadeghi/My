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

    public class CaseInfo {

        private int caseID;
        private int caseTypeID;
        private int customerID;
        private int invoiceID;
        private int priority;
        private int userID;
        private int closeUserID;
        private int deletedUserID;
        private int parentID;
        private int commentCount;
        private int sumPayment;
        private int assignCount;
        private int attachCount;
        private int assignID;
        private long deletedTime;
        private long closeTime;
        private long addTime;
        private boolean share;
        private boolean resultOk;
        private String caseType;
        private String customerName;
        private String userFullName;
        private String description;
        private String closeUserFullName;
        private String resultDescription;
        private String deletedUserFullName;
        private String assignDescription;

        private CaseProductResult.CaseProductInfo[] caseProduct;
        private AssignResult.AssignInfo[] assigns;

        public int getCaseID() {
            return caseID;
        }

        public void setCaseID(int caseID) {
            this.caseID = caseID;
        }

        public int getCaseTypeID() {
            return caseTypeID;
        }

        public void setCaseTypeID(int caseTypeID) {
            this.caseTypeID = caseTypeID;
        }

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public int getInvoiceID() {
            return invoiceID;
        }

        public void setInvoiceID(int invoiceID) {
            this.invoiceID = invoiceID;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
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

        public int getDeletedUserID() {
            return deletedUserID;
        }

        public void setDeletedUserID(int deletedUserID) {
            this.deletedUserID = deletedUserID;
        }

        public int getParentID() {
            return parentID;
        }

        public void setParentID(int parentID) {
            this.parentID = parentID;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getSumPayment() {
            return sumPayment;
        }

        public void setSumPayment(int sumPayment) {
            this.sumPayment = sumPayment;
        }

        public int getAssignCount() {
            return assignCount;
        }

        public void setAssignCount(int assignCount) {
            this.assignCount = assignCount;
        }

        public int getAttachCount() {
            return attachCount;
        }

        public void setAttachCount(int attachCount) {
            this.attachCount = attachCount;
        }

        public int getAssignID() {
            return assignID;
        }

        public void setAssignID(int assignID) {
            this.assignID = assignID;
        }

        public long getDeletedTime() {
            return deletedTime;
        }

        public void setDeletedTime(long deletedTime) {
            this.deletedTime = deletedTime;
        }

        public long getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(long closeTime) {
            this.closeTime = closeTime;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public boolean isShare() {
            return share;
        }

        public void setShare(boolean share) {
            this.share = share;
        }

        public boolean isResultOk() {
            return resultOk;
        }

        public void setResultOk(boolean resultOk) {
            this.resultOk = resultOk;
        }

        public String getCaseType() {
            return caseType;
        }

        public void setCaseType(String caseType) {
            this.caseType = caseType;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
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

        public String getResultDescription() {
            return resultDescription;
        }

        public void setResultDescription(String resultDescription) {
            this.resultDescription = resultDescription;
        }

        public String getDeletedUserFullName() {
            return deletedUserFullName;
        }

        public void setDeletedUserFullName(String deletedUserFullName) {
            this.deletedUserFullName = deletedUserFullName;
        }

        public String getAssignDescription() {
            return assignDescription;
        }

        public void setAssignDescription(String assignDescription) {
            this.assignDescription = assignDescription;
        }

        public CaseProductResult.CaseProductInfo[] getCaseProduct() {
            return caseProduct;
        }

        public AssignResult.AssignInfo[] getAssigns() {
            return assigns;
        }
    }
}
