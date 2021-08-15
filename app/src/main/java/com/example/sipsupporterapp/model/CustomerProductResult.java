package com.example.sipsupporterapp.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.sipsupporterapp.utils.Converter;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomerProductResult {
    private String error;
    private String errorCode;
    private CustomerProductInfo[] customerProducts;

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

    public CustomerProductInfo[] getCustomerProducts() {
        return customerProducts;
    }

    public void setCustomerProducts(CustomerProductInfo[] customerProducts) {
        this.customerProducts = customerProducts;
    }

    public static class CustomerProductInfo {
        private int customerProductID;
        private int customerID;
        private int productID;
        private int userID;
        private int finishUserID;
        private int invoicePaymentUserID;
        private long invoicePaymentTime;
        private long invoicePrice;
        private long expireDate;
        private long addTime;
        private long finishTime;
        private boolean finish;
        private boolean invoicePayment;
        private String customerName;
        private String productName;
        private String description;
        private String userFullName;
        private String finishUserFullName;
        private String invoicePaymentUserFullName;

        public int getCustomerProductID() {
            return customerProductID;
        }

        public void setCustomerProductID(int customerProductID) {
            this.customerProductID = customerProductID;
        }

        public int getCustomerID() {
            return customerID;
        }

        public void setCustomerID(int customerID) {
            this.customerID = customerID;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public int getFinishUserID() {
            return finishUserID;
        }

        public void setFinishUserID(int finishUserID) {
            this.finishUserID = finishUserID;
        }

        public int getInvoicePaymentUserID() {
            return invoicePaymentUserID;
        }

        public void setInvoicePaymentUserID(int invoicePaymentUserID) {
            this.invoicePaymentUserID = invoicePaymentUserID;
        }

        public long getInvoicePaymentTime() {
            return invoicePaymentTime;
        }

        public void setInvoicePaymentTime(long invoicePaymentTime) {
            this.invoicePaymentTime = invoicePaymentTime;
        }

        public long getInvoicePrice() {
            return invoicePrice;
        }

        public void setInvoicePrice(long invoicePrice) {
            this.invoicePrice = invoicePrice;
        }

        public long getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(long expireDate) {
            this.expireDate = expireDate;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public boolean isInvoicePayment() {
            return invoicePayment;
        }

        public void setInvoicePayment(boolean invoicePayment) {
            this.invoicePayment = invoicePayment;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
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

        public String getFinishUserFullName() {
            return finishUserFullName;
        }

        public void setFinishUserFullName(String finishUserFullName) {
            this.finishUserFullName = finishUserFullName;
        }

        public String getInvoicePaymentUserFullName() {
            return invoicePaymentUserFullName;
        }

        public void setInvoicePaymentUserFullName(String invoicePaymentUserFullName) {
            this.invoicePaymentUserFullName = invoicePaymentUserFullName;
        }

        @BindingAdapter({"currencyFormat"})
        public static void setCurrencyFormat(TextView textView, long invoicePrice) {
            String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(invoicePrice);
            textView.setText(currencyFormat + "تومان");
        }

        @BindingAdapter({"convertLetter"})
        public static void converter(TextView textView, String text) {
            textView.setText(Converter.letterConverter(text));
        }
    }
}
