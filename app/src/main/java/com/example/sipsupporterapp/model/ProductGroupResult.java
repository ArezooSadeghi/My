package com.example.sipsupporterapp.model;

public class ProductGroupResult {
    private String errorCode;
    private String error;
    private ProductGroupInfo[] productGroups;

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

    public ProductGroupInfo[] getProductGroups() {
        return productGroups;
    }

    public void setProductGroups(ProductGroupInfo[] productGroups) {
        this.productGroups = productGroups;
    }

    public class ProductGroupInfo {
        private int productGroupID;
        private int userID;
        private int parentID;
        private long addTime;
        private long editTime;
        private String productGroup;
        private String userFullName;
        private ProductResult.ProductInfo[] products;

        public int getProductGroupID() {
            return productGroupID;
        }

        public void setProductGroupID(int productGroupID) {
            this.productGroupID = productGroupID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getParentID() {
            return parentID;
        }

        public void setParentID(int parentID) {
            this.parentID = parentID;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public long getEditTime() {
            return editTime;
        }

        public void setEditTime(long editTime) {
            this.editTime = editTime;
        }

        public String getProductGroup() {
            return productGroup;
        }

        public void setProductGroup(String productGroup) {
            this.productGroup = productGroup;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public ProductResult.ProductInfo[] getProducts() {
            return products;
        }

        public void setProducts(ProductResult.ProductInfo[] products) {
            this.products = products;
        }
    }
}
