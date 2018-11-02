package l2f.commons.lang;

public class StringUtils {
    public static final String EMPTY = "";

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String defaultString(String text) {
        if (text != null && text.length() > 0)
            return text;
        return EMPTY;
    }

    public static boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }
}
