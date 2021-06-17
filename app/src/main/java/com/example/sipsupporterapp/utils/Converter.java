package com.example.sipsupporterapp.utils;

public class Converter {

    public static String convert(String input) {
        String output = "";
        if (input == null) {
            return output;
        }
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == 'ي') {
                output += 'ی';
            } else {
                output += input.charAt(i);
            }
        }
        return output;
    }
}
