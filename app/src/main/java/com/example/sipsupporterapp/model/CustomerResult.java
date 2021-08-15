package com.example.sipsupporterapp.model;

public class CustomerResult {
    private String error;
    private String errorCode;
    private CustomerInfo[] customers;

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

    public CustomerInfo[] getCustomers() {
        return customers;
    }

    public void setCustomers(CustomerInfo[] customers) {
        this.customers = customers;
    }

    public static class CustomerInfo {
        private int customerID;
        private String customerName;
        private String tel;
        private String province;
        private String city;
        private String lastSeen;

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getLastSeen() {
            return lastSeen;
        }

        public void setLastSeen(String lastSeen) {
            this.lastSeen = lastSeen;
        }
    }
}
