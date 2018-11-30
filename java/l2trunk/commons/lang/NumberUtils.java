package l2trunk.commons.lang;

public final class NumberUtils {

    public static int toInt(String string) {
        return toInt(string, 0);
    }

    public static int toInt(String string, int alternative) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return alternative;
        }
    }

    public static boolean isNumber(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static long toLong(String string, long alternative) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return alternative;
        }
    }

    public static double toDouble(String string) {
        return toDouble(string, 0.0);
    }

    public static double toDouble(String string, double alternative) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return alternative;
        }
    }
}
