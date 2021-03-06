package com.example.sipsupporterapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SipSupportSharedPreferences {

    private static final String USER_FULL_NAME = "userFullName";
    private static final String USER_LOGIN_KEY = "userLoginKey";
    private static final String LAST_VALUE_SPINNER = "lastValueSpinner";
    private static final String CUSTOMER_NAME = "customerName";
    private static final String USER_NAME = "userName";
    private static final String CUSTOMER_TEL = "customerTel";
    private static final String DATE = "date";
    private static final String FACTOR = "factor";
    private static final String CASE_ID = "caseID";

    public static void setUserFullName(Context context, String userFullName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(USER_FULL_NAME, userFullName).commit();
    }

    public static String getUserFullName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(USER_FULL_NAME, null);
    }

    public static void setUserLoginKey(Context context, String userLoginKey) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(USER_LOGIN_KEY, userLoginKey).commit();
    }

    public static String getUserLoginKey(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(USER_LOGIN_KEY, null);
    }

    public static void setCenterName(Context context, String value) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(LAST_VALUE_SPINNER, value).commit();
    }

    public static String getCenterName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(LAST_VALUE_SPINNER, null);
    }

    public static void setCustomerName(Context context, String customerName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(CUSTOMER_NAME, customerName).commit();
    }

    public static String getCustomerName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(CUSTOMER_NAME, null);
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(USER_NAME, userName).commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(USER_NAME, null);
    }

    public static void setCustomerTel(Context context, String customerTel) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(CUSTOMER_TEL, customerTel).commit();
    }

    public static String getCustomerTel(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(CUSTOMER_TEL, null);
    }

    public static void setDate(Context context, String date) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(DATE, date).commit();
    }

    public static String getDate(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(DATE, null);
    }

    public static void setFactor(Context context, String factor) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(FACTOR, factor).commit();
    }

    public static String getFactor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(FACTOR, null);
    }

    public static void setCaseID(Context context, int caseID) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putInt(CASE_ID, caseID).commit();
    }

    public static int getCaseID(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getInt(CASE_ID, 0);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getPackageName(),
                context.MODE_PRIVATE);
    }
}
