package com.example.sipsupporterapp.utils;

public class Converter {

    public static String letterConverter(String input) {
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

    public static String numberConverter(String input) {
        return input.replaceAll("", "")
                .replaceAll("۰", "0")
                .replaceAll("۱", "1")
                .replaceAll("۲", "2")
                .replaceAll("۳", "3")
                .replaceAll("۴", "4")
                .replaceAll("۵", "5")
                .replaceAll("۶", "6")
                .replaceAll("۷", "7")
                .replaceAll("۸", "8")
                .replaceAll("۹", "9")
                .replaceAll(" ", "");
    }

    public static boolean hasEnglishLetter(String input) {
        String output = "";
        char[] chars = input.toCharArray();
        for (Character character : chars) {
            if (!String.valueOf(character).equals(".")) {
                output += String.valueOf(character);
            }
        }

        if (output.matches(".*[a-zA-Z]+.*")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean hasThreeDots(String intput) {
        int dotNumber = 0;
        char[] chars = intput.toCharArray();
        for (Character character : chars) {
            if (String.valueOf(character).equals(".")) {
                dotNumber++;
            }
        }
        if (dotNumber == 3) {
            return true;
        } else {
            return false;
        }
    }
}

