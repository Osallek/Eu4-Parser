package com.osallek.eu4parser.common;

public class NumbersUtils {

    private NumbersUtils() {}

    public static Integer toInt(final String str) {
        if (str == null) {
            return null;
        }

        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

    public static Double toDouble(final String str) {
        if (str == null) {
            return null;
        }

        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }
}
