package fr.osallek.eu4parser.common;

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
