package l2f.commons.lang;

public class NumberUtils {

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
        } catch (NumberFormatException e ) {
            return false;
        }
        return true;
    }

    public static long toLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double toDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
