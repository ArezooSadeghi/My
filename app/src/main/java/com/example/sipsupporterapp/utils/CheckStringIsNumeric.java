package com.example.sipsupporterapp.utils;

import java.util.regex.Pattern;

public class CheckStringIsNumeric {

    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
