package com.example.sipsupporterapp.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.sipsupporterapp.utils.Converter;

public class CustomerUserResult {
    private String error;
    private String errorCode;
    private CustomerUserInfo[] customerUsers;

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

    public CustomerUserInfo[] getCustomerUsers() {
        return customerUsers;
    }

    public void setCustomerUsers(CustomerUserInfo[] customerUsers) {
        this.customerUsers = customerUsers;
    }

    public static class CustomerUserInfo {
        private int customerUserID;
        private int customerID;
        private int userID;
        private String userName;
        private String lastSeen;

        public int getCustomerUserID() {
            return customerUserID;
        }

        public void setCustomerUserID(int customerUserID) {
            this.customerUserID = customerUserID;
        }

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getLastSeen() {
            return lastSeen;
        }

        public void setLastSeen(String lastSeen) {
            this.lastSeen = lastSeen;
        }

        @BindingAdapter({"convertLetter"})
        public static void converter(TextView textView, String text) {
            textView.setText(Converter.letterConverter(text));
        }
    }
}
