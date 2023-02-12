package fr.osallek.eu4parser.common;

import java.util.Optional;

public class NumbersUtils {

    private NumbersUtils() {}

    public static Optional<Integer> parseInt(String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    public static Integer toInt(String s) {
        return parseInt(s).orElse(null);
    }

    public static Double toDouble(String s) {
        if (s == null) {
            return null;
        }

        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static Integer doubleToInt(Double aDouble) {
        return aDouble == null ? null : aDouble.intValue();
    }

    public static int intOrDefault(Integer integer) {
        return intOrDefault(integer, 0);
    }

    public static int intOrDefault(Integer integer, int defaultInt) {
        return integer == null ? defaultInt : integer;
    }

    public static int toIntOrDefault(String s) {
        return toIntOrDefault(s, 0);
    }

    public static int toIntOrDefault(String s, int defaultInt) {
        Integer integer = toInt(s);
        return integer == null ? defaultInt : integer;
    }

    public static double doubleOrDefault(Double aDouble) {
        return doubleOrDefault(aDouble, 0);
    }

    public static double doubleOrDefault(Double aDouble, double defaultDouble) {
        return aDouble == null ? defaultDouble : aDouble;
    }

    public static double numberOrDefault(Number number) {
        return numberOrDefault(number, 0);
    }

    public static double numberOrDefault(Number number, double defaultDouble) {
        return number == null ? defaultDouble : number.doubleValue();
    }
}
