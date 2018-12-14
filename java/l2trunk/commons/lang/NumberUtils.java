package l2trunk.commons.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public final class NumberUtils {
    private static final List<String> True = Arrays.asList("y", "yes", "true", "1");
    private static final List<String> False = Arrays.asList("n", "no", "false", "0");

    private static final Logger LOG = LoggerFactory.getLogger(NumberUtils.class);

    public static int toInt(String string) {
        return toInt(string, 0);
    }

    public static int toInt(String string, int alternative) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            LOG.warn("Error parsing:" + string + " to int");
            return alternative;
        }
    }

    public static long toLong(String string) {
        return toLong(string, 0);
    }

    public static boolean toBoolean(String string) {
        return toBoolean(string, false);
    }

    public static boolean toBoolean(String string, boolean alternative) {
        if (True.contains(string.toLowerCase())) return true;
        if (False.contains(string.toLowerCase())) return false;
        return alternative;

    }

    public static long toLong(String string, long alternative) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            LOG.warn("Error parsing:" + string + " to int");
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

    public static double toDouble(String string) {
        return toDouble(string, 0.0);
    }

    public static double toDouble(String string, double alternative) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            LOG.warn("Error parsing:" + string + " to double");
            return alternative;
        }
    }
}
