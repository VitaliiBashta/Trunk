package l2trunk.gameserver.utils;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public final class Util {
    private static final String PATTERN = "0.0000000000E00";
    private static final DecimalFormat df;
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final NumberFormat adenaFormatter;
    private static final Pattern _pattern = Pattern.compile("<!--TEMPLET(\\d+)(.*?)TEMPLET-->", Pattern.DOTALL);

    static {
        adenaFormatter = NumberFormat.getIntegerInstance(Locale.FRANCE);
        df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.applyPattern(PATTERN);
        df.setPositivePrefix("+");
    }

    public static boolean isMatchingRegexp(String text, String template) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(template);
        } catch (PatternSyntaxException e) // invalid template
        {
            e.printStackTrace();
        }
        if (pattern == null)
            return false;
        Matcher regexp = pattern.matcher(text);
        return regexp.matches();
    }

    public static String formatDouble(double x, String nanString, boolean forceExponents) {
        if (Double.isNaN(x))
            return nanString;
        if (forceExponents)
            return df.format(x);
        if ((long) x == x)
            return String.valueOf((long) x);
        return String.valueOf(x);
    }

    public static String formatAdena(long amount) {
        return adenaFormatter.format(amount);
    }

    public static String formatTime(int time) {
        if (time == 0)
            return "now";
        time = Math.abs(time);
        String ret = "";
        long numDays = time / 86400;
        time -= numDays * 86400;
        long numHours = time / 3600;
        time -= numHours * 3600;
        long numMins = time / 60;
        time -= numMins * 60;
        long numSeconds = time;
        if (numDays > 0)
            ret += numDays + "d ";
        if (numHours > 0)
            ret += numHours + "h ";
        if (numMins > 0)
            ret += numMins + "m ";
        if (numSeconds > 0)
            ret += numSeconds + "s";
        return ret.trim();
    }

    public static long rollDrop(long min, long max, double calcChance, boolean rate) {
        if (calcChance <= 0 || min <= 0 || max <= 0)
            return 0;
        int dropmult = 1;
        if (rate)
            calcChance *= Config.RATE_DROP_ITEMS;
        if (calcChance > RewardList.MAX_CHANCE)
            if (calcChance % RewardList.MAX_CHANCE == 0)
                dropmult = (int) (calcChance / RewardList.MAX_CHANCE);
            else {
                dropmult = (int) Math.ceil(calcChance / RewardList.MAX_CHANCE);
                calcChance = calcChance / dropmult;
            }
        return Rnd.chance(calcChance / 10000.) ? Rnd.get(min * dropmult, max * dropmult) : 0;
    }

    public static int packInt(int[] a, int bits) throws Exception {
        int m = 32 / bits;
        if (a.length > m)
            throw new Exception("Overflow");

        int result = 0;
        int next;
        int mval = (int) Math.pow(2, bits);
        for (int i = 0; i < m; i++) {
            result <<= bits;
            if (a.length > i) {
                next = a[i];
                if (next >= mval || next < 0)
                    throw new Exception("Overload, value is out of range");
            } else
                next = 0;
            result += next;
        }
        return result;
    }

    public static long packLong(int[] a, int bits) throws Exception {
        int m = 64 / bits;
        if (a.length > m)
            throw new Exception("Overflow");

        long result = 0;
        int next;
        int mval = (int) Math.pow(2, bits);
        for (int i = 0; i < m; i++) {
            result <<= bits;
            if (a.length > i) {
                next = a[i];
                if (next >= mval || next < 0)
                    throw new Exception("Overload, value is out of range");
            } else
                next = 0;
            result += next;
        }
        return result;
    }

    public static Integer[] unpackInt(int a, int bits) {
        int m = 32 / bits;
        int mval = (int) Math.pow(2, bits);
        Integer[] result = new Integer[m];
        int next;
        for (int i = m; i > 0; i--) {
            next = a;
            a = a >> bits;
            result[i - 1] = next - a * mval;
        }
        return result;
    }

    public static int[] unpackLong(long a, int bits) {
        int m = 64 / bits;
        int mval = (int) Math.pow(2, bits);
        int[] result = new int[m];
        long next;
        for (int i = m; i > 0; i--) {
            next = a;
            a = a >> bits;
            result[i - 1] = (int) (next - a * mval);
        }
        return result;
    }

    public static float[] parseCommaSeparatedFloatArray(String s) {
        if (s.isEmpty())
            return new float[0];
        String[] tmp = s.replaceAll(",", ";").replaceAll("\\n", ";").split(";");
        float[] val = new float[tmp.length];
        for (int i = 0; i < tmp.length; i++)
            val[i] = Float.parseFloat(tmp[i]);
        return val;
    }

    public static int[] parseCommaSeparatedIntegerArray(String s) {
        if (s.isEmpty())
            return new int[0];
        String[] tmp = s.replaceAll(",", ";").replaceAll("\\n", ";").split(";");
        int[] val = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++)
            val[i] = Integer.parseInt(tmp[i]);
        return val;
    }

    private static long[] parseCommaSeparatedLongArray(String s) {
        if (s.isEmpty())
            return new long[0];
        String[] tmp = s.replaceAll(",", ";").replaceAll("\\n", ";").split(";");
        long[] val = new long[tmp.length];
        for (int i = 0; i < tmp.length; i++)
            val[i] = Long.parseLong(tmp[i]);
        return val;
    }

    public static long[][] parseStringForDoubleArray(String s) {
        String[] temp = s.replaceAll("\\n", ";").split(";");
        long[][] val = new long[temp.length][];

        for (int i = 0; i < temp.length; i++)
            val[i] = parseCommaSeparatedLongArray(temp[i]);
        return val;
    }

    /**
     * Just alias
     *
     * @param glueStr
     * @param strings
     * @param startIdx
     * @param maxCount
     * @return
     */
    public static String joinStrings(String glueStr, String[] strings, int startIdx, int maxCount) {
        return Strings.joinStrings(glueStr, strings, startIdx, maxCount);
    }

    /**
     * Just alias
     *
     * @param glueStr
     * @param strings
     * @param startIdx
     * @return
     */
    public static String joinStrings(String glueStr, String[] strings, int startIdx) {
        return Strings.joinStrings(glueStr, strings, startIdx, -1);
    }

    public static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String dumpObject(Object o, boolean simpleTypes, boolean parentFields, boolean ignoreStatics) {
        Class<?> cls = o.getClass();
        String val, type, result = "[" + (simpleTypes ? cls.getSimpleName() : cls.getName()) + "\n";
        Object fldObj;
        List<Field> fields = new ArrayList<>();
        while (cls != null) {
            for (Field fld : cls.getDeclaredFields())
                if (!fields.contains(fld)) {
                    if (ignoreStatics && Modifier.isStatic(fld.getModifiers()))
                        continue;
                    fields.add(fld);
                }
            cls = cls.getSuperclass();
            if (!parentFields)
                break;
        }

        for (Field fld : fields) {
            fld.setAccessible(true);
            try {
                fldObj = fld.get(o);
                if (fldObj == null)
                    val = "NULL";
                else
                    val = fldObj.toString();
            } catch (Throwable e) {
                e.printStackTrace();
                val = "<ERROR>";
            }
            type = simpleTypes ? fld.getType().getSimpleName() : fld.getType().toString();

            result += String.format("\t%s [%s] = %s;\n", fld.getName(), type, val);
        }

        result += "]\n";
        return result;
    }

    public static HashMap<Integer, String> parseTemplate(String html) {
        Matcher m = _pattern.matcher(html);
        HashMap<Integer, String> tpls = new HashMap<>();
        while (m.find()) {
            tpls.put(Integer.parseInt(m.group(1)), m.group(2));
            html = html.replace(m.group(0), "");
        }

        tpls.put(0, html);
        return tpls;
    }

    public static boolean isDigit(String text) {
        if (text == null)
            return false;
        return text.matches("[0-9]+");
    }

    /**
     * @param raw
     * @return
     */
    public static String printData(byte[] raw) {
        return printData(raw, raw.length);
    }

    private static String fillHex(int data, int digits) {
        String number = Integer.toHexString(data);
        for (int i = number.length(); i < digits; i++) {
            number = "0" + number;
        }
        return number;
    }

    private static String printData(byte[] data, int len) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        for (int i = 0; i < len; i++) {
            if (counter % 16 == 0) {
                result.append(fillHex(i, 4) + ": ");
            }
            result.append(fillHex(data[i] & 0xff, 2) + " ");
            counter++;
            if (counter == 16) {
                result.append("   ");
                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    int t1 = data[charpoint++];
                    if (t1 > 0x1f && t1 < 0x80) {
                        result.append((char) t1);
                    } else {
                        result.append('.');
                    }
                }
                result.append("\n");
                counter = 0;
            }
        }
        int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < 17 - rest; i++) {
                result.append("   ");
            }
            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                int t1 = data[charpoint++];
                if (t1 > 0x1f && t1 < 0x80) {
                    result.append((char) t1);
                } else {
                    result.append('.');
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static byte[] generateHex(int size) {
        byte[] array = new byte[size];
        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = (byte) rnd.nextInt(256);
        }
        return array;
    }

    /**
     * @param number
     * @return From 123123 returns 123,123
     */
    public static String getNumberWithCommas(long number) {
        String text = String.valueOf(number);
        int size = text.length();
        for (int i = size; i > 0; i--) {
            if ((size - i) % 3 == 0 && i < size) {
                text = text.substring(0, i) + ',' + text.substring(i);
            }
        }
        return text;
    }

    public static String getFullClassName(ClassId classIndex) {
        return classIndex.getName();
    }

    public static String getFullClassName(int classId) {
        return Stream.of(ClassId.values()).filter(e -> e.id() == classId)
                .map(ClassId::getName)
                .findFirst().orElse("Unknown");
    }

    public static String boolToString(boolean b) {
        return b ? "True" : "False";
    }

    public static boolean isInteger(char c) {
        return Character.isDigit(c);
    }

    public static String toProperCaseAll(String name) {
        StringTokenizer st = new StringTokenizer(name);
        String newString = "";

        newString = st.nextToken();
        name = newString.substring(0, 1).toUpperCase();
        if (newString.length() > 1)
            name += newString.substring(1).toLowerCase();

        while (st.hasMoreTokens()) {
            newString = st.nextToken();

            if (newString.length() > 2) {
                name += " " + newString.substring(0, 1).toUpperCase();
                name += newString.substring(1).toLowerCase();
            } else
                name += " " + newString;
        }

        return name;
    }

    public static String convertToLineagePriceFormat(double price) {
        if (price < 10000)
            return Math.round(price) + " Adena";
        else if (price < 1000000)
            return Util.reduceDecimals(price / 1000, 1) + " k";
        else if (price < 1000000000)
            return Util.reduceDecimals(price / 1000 / 1000, 1) + " kk";
        else
            return Util.reduceDecimals(price / 1000 / 1000 / 1000, 1) + " kkk";
    }

    private static String reduceDecimals(double original, int nDecim) {
        return reduceDecimals(original, nDecim, false);
    }

    private static String reduceDecimals(double original, int nDecim, boolean round) {
        String decimals = "#";
        if (nDecim > 0) {
            decimals += ".";
            for (int i = 0; i < nDecim; i++)
                decimals += "#";
        }

        final DecimalFormat df = new DecimalFormat(decimals);
        return df.format((round ? Math.round(original) : original)).replace(",", ".");
    }

    public static double calculateDistance(Creature obj1, Creature obj2, boolean includeZAxis) {
        if (obj1 == null || obj2 == null)
            return 1000000;

        return calculateDistance(obj1.getX(), obj1.getY(), obj1.getZ(), obj2.getX(), obj2.getY(), obj2.getZ(), includeZAxis);
    }

    /**
     * @param includeZAxis - if true, includes also the Z axis in the calculation
     * @return the distance between the two coordinates
     */
    private static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis) {
        double dx = (double) x1 - x2;
        double dy = (double) y1 - y2;

        if (includeZAxis) {
            final double dz = z1 - z2;
            return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
        }
        return Math.sqrt(dx * dx + dy * dy);
    }


    public static String time(long time) {
        return TIME_FORMAT.format(new Date(time));
    }

    public static void communityNextPage(Player player, String link) {
        ICommunityBoardHandler handler = CommunityBoardManager.getCommunityHandler(link);
        if (handler != null)
            handler.onBypassCommand(player, link);
    }

    public static String getItemName(int itemId) {
        if (itemId == ItemTemplate.ITEM_ID_FAME)
            return "Fame";
        else if (itemId == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
            return "PC Bang point";
        else if (itemId == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
            return "Clan reputation";
        else
            return ItemHolder.getTemplate(itemId).getName();
    }

    public static String getItemIcon(int itemId) {
        return ItemHolder.getTemplate(itemId).getIcon();
    }

    public static String formatPay(Player player, long count, int item) {
        if (count > 0)
            return formatAdena(count) + " " + getItemName(item);
        else
            return "Free";
    }

    public static String declension(long count, DeclensionKey word) {
        String one = "";
        String two = "";
        String five = "";
        switch (word) {
            case DAYS:
                one = "Day";
                two = "Days";
                five = "Days";
                break;
            case HOUR:
                one = "Hour";
                two = "Hours";
                five = "Hours";
                break;
            case MINUTES:
                one = "Minute";
                two = "Minutes";
                five = "Minutes";
                break;
            case PIECE:
                one = "Piece";
                two = "Pieces";
                five = "Pieces";
                break;
            case POINT:
                one = "Point";
                two = "Points";
                five = "Points";
        }
        if (count > 100L) {
            count %= 100L;
        }
        if (count > 20L) {
            count %= 10L;
        }
        if (count == 1L) {
            return one;
        }
        if ((count == 2L) || (count == 3L) || (count == 4L)) {
            return two;
        }
        return five;
    }

    public static long addDay(long count) {
        return count * 1000 * 60 * 60 * 24;
    }

    public static double cutOff(double num, int pow) {
        return (int) (num * Math.pow(10, pow)) / Math.pow(10, pow);
    }

    public static boolean getPay(Player player, int itemid, long count, boolean sendMessage) {
        if (count == 0) {
            return true;
        }
        boolean check = false;
        switch (itemid) {
            case -300:
                if (player.getFame() >= count) {
                    player.addFame(-(int) count, "Disappeared: {0}.");
                    check = true;
                }
                break;
            case -200:
                if ((player.getClan() != null) && (player.getClan().getLevel() >= 5) && (player.getClan().getLeader().isClanLeader()) && (player.getClan().getReputationScore() >= count)) {
                    player.getClan().incReputation((int) -count, false, "Disappeared: {0}.");
                    check = true;
                }
                break;
            case -100:
                if (player.getPcBangPoints() >= count) {
                    if (player.reducePcBangPoints((int) count)) {
                        check = true;
                    }
                }
                break;
            default:
                if (player.getInventory().getCountOf(itemid) >= count) {
                    if (player.getInventory().destroyItemByItemId(itemid, count, "deleted")) {
                        check = true;
                    }
                }
                break;
        }
        if (!check) {
            if (sendMessage) {
                enoughtItem(player, itemid, count);
            }
            return false;
        }
        if (sendMessage) {
            player.sendMessage("Disappeared: " + formatPay(player, count, itemid) + ".");
        }
        return true;
    }

    private static void enoughtItem(Player player, int itemid, long count) {
        player.sendPacket(new ExShowScreenMessage("You do not have " + formatPay(player, count, itemid) + "."));
        player.sendMessage("You do not have " + formatPay(player, count, itemid) + ".");
    }

}
