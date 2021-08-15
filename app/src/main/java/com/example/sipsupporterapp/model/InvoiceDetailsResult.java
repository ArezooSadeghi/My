package com.example.sipsupporterapp.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.sipsupporterapp.utils.Converter;

import java.text.NumberFormat;
import java.util.Locale;

public class InvoiceDetailsResult {
    private String errorCode;
    private String error;
    private InvoiceDetailsInfo[] invoiceDetails;

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

    public InvoiceDetailsInfo[] getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(InvoiceDetailsInfo[] invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }

    public static class InvoiceDetailsInfo {
        private int invoiceDetailsID;
        private int invoiceID;
        private int productID;
        private int QTY;
        private int unitPrice;
        private int sumDiscountPrice;
        private int userID;
        private long addTime;
        private String userFullName;
        private String productName;
        private String description;

        public int getInvoiceDetailsID() {
            return invoiceDetailsID;
        }

        public void setInvoiceDetailsID(int invoiceDetailsID) {
            this.invoiceDetailsID = invoiceDetailsID;
        }

        public int getInvoiceID() {
            return invoiceID;
        }

        public void setInvoiceID(int invoiceID) {
            this.invoiceID = invoiceID;
        }

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public int getQTY() {
            return QTY;
        }

        public void setQTY(int QTY) {
            this.QTY = QTY;
        }

        public int getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(int unitPrice) {
            this.unitPrice = unitPrice;
        }

        public int getSumDiscountPrice() {
            return sumDiscountPrice;
        }

        public void setSumDiscountPrice(int sumDiscountPrice) {
            this.sumDiscountPrice = sumDiscountPrice;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public void setUserFullName(String userFullName) {
            this.userFullName = userFullName;
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

        @BindingAdapter(value = {"currencyFormat", "suffix"})
        public static void setCurrencyFormat(TextView textView, int price, String suffix) {
            String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(price);
            textView.setText(suffix + currencyFormat);
        }

        @BindingAdapter({"sum"})
        public static void calculateSum(TextView textView, InvoiceDetailsInfo invoiceDetailsInfo) {
            int sum = ((invoiceDetailsInfo.getQTY() * invoiceDetailsInfo.getUnitPrice()) - invoiceDetailsInfo.getSumDiscountPrice());
            String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(sum);
            textView.setText("جمع:" + currencyFormat);
        }

        @BindingAdapter({"convertLetter"})
        public static void converter(TextView textView, String text) {
            if (text.length() > 5) {
                textView.setText(Converter.letterConverter(text).substring(0, 6) + "...");
            } else {
                textView.setText(Converter.letterConverter(text));
            }
        }
    }
}
