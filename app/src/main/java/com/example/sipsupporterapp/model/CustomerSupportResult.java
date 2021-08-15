package com.example.sipsupporterapp.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.sipsupporterapp.utils.Converter;

public class CustomerSupportResult {
    private String error;
    private String errorCode;
    private CustomerSupportInfo[] customerSupports;

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

    public CustomerSupportInfo[] getCustomerSupports() {
        return customerSupports;
    }

    public void setCustomerSupports(CustomerSupportInfo[] customerSupports) {
        this.customerSupports = customerSupports;
    }

    public static class CustomerSupportInfo {
        private int customerSupportID;
        private int supportEventID;
        private int customerID;
        private int customerUserID;
        private int userID;
        private String supportEvent;
        private String customerName;
        private String customerUserName;
        private String question;
        private String regTime;
        private String userFullName;
        private String answer;
        private String answerRegTime;

        public int getCustomerSupportID() {
            return customerSupportID;
        }

        public void setCustomerSupportID(int customerSupportID) {
            this.customerSupportID = customerSupportID;
        }

        public int getSupportEventID() {
            return supportEventID;
        }

        public void setSupportEventID(int supportEventID) {
            this.supportEventID = supportEventID;
        }

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public int getCustomerUserID() {
            return customerUserID;
        }

        public void setCustomerUserID(int customerUserID) {
            this.customerUserID = customerUserID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getSupportEvent() {
            return supportEvent;
        }

        public void setSupportEvent(String supportEvent) {
            this.supportEvent = supportEvent;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerUserName() {
            return customerUserName;
        }

        public void setCustomerUserName(String customerUserName) {
            this.customerUserName = customerUserName;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getRegTime() {
            return regTime;
        }

        public void setRegTime(String regTime) {
            this.regTime = regTime;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getAnswerRegTime() {
            return answerRegTime;
        }

        public void setAnswerRegTime(String answerRegTime) {
            this.answerRegTime = answerRegTime;
        }

        @BindingAdapter({"convertLetter"})
        public static void converter(TextView textView, String text) {
            textView.setText(Converter.letterConverter(text));
        }
    }
}
