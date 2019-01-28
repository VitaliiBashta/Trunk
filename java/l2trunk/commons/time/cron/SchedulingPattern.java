package l2trunk.commons.time.cron;

import java.util.*;

public final class SchedulingPattern {

    private static final int MINUTE_MIN_VALUE = 0;
    private static final int MINUTE_MAX_VALUE = 59;
    private static final int HOUR_MIN_VALUE = 0;
    private static final int HOUR_MAX_VALUE = 23;
    private static final int DAY_OF_MONTH_MIN_VALUE = 1;
    private static final int DAY_OF_MONTH_MAX_VALUE = 31;
    private static final int MONTH_MIN_VALUE = 1;
    private static final int MONTH_MAX_VALUE = 12;
    private static final int DAY_OF_WEEK_MIN_VALUE = 0;
    private static final int DAY_OF_WEEK_MAX_VALUE = 7;

    /**
     * The parser for the minute values.
     */
    private static final ValueParser MINUTE_VALUE_PARSER = new MinuteValueParser();

    /**
     * The parser for the hour values.
     */
    private static final ValueParser HOUR_VALUE_PARSER = new HourValueParser();

    /**
     * The parser for the day of month values.
     */
    private static final ValueParser DAY_OF_MONTH_VALUE_PARSER = new DayOfMonthValueParser();

    /**
     * The parser for the month values.
     */
    private static final ValueParser MONTH_VALUE_PARSER = new MonthValueParser();

    /**
     * The parser for the day of week values.
     */
    private static final ValueParser DAY_OF_WEEK_VALUE_PARSER = new DayOfWeekValueParser();
    /**
     * The ValueMatcher list for the "minute" field.
     */
    private final List<ValueMatcher> minuteMatchers = new ArrayList<>();
    /**
     * The ValueMatcher list for the "hour" field.
     */
    private final List<ValueMatcher> hourMatchers = new ArrayList<>();
    /**
     * The ValueMatcher list for the "day of month" field.
     */
    private final List<ValueMatcher> dayOfMonthMatchers = new ArrayList<>();
    /**
     * The ValueMatcher list for the "month" field.
     */
    private final List<ValueMatcher> monthMatchers = new ArrayList<>();
    /**
     * The ValueMatcher list for the "day of week" field.
     */
    private final List<ValueMatcher> dayOfWeekMatchers = new ArrayList<>();
    /**
     * How many matcher groups in this pattern?
     */
    private int matcherSize = 0;
    /**
     * The pattern as a string.
     */
    private final String asString;

    /**
     * Builds a SchedulingPattern parsing it from a string.
     *
     * @param pattern The pattern as a crontab-like string.
     * @throws InvalidPatternException If the supplied string is not a valid pattern.
     */
    public SchedulingPattern(String pattern) throws InvalidPatternException {
        this.asString = pattern;
        StringTokenizer st1 = new StringTokenizer(pattern, "|");
        if (st1.countTokens() < 1) {
            throw new InvalidPatternException("invalid pattern: \"" + pattern + "\"");
        }
        while (st1.hasMoreTokens()) {
            String localPattern = st1.nextToken();
            StringTokenizer st2 = new StringTokenizer(localPattern, " \t");
            if (st2.countTokens() != 5) {
                throw new InvalidPatternException("invalid pattern: \"" + localPattern + "\"");
            }
            try {
                minuteMatchers.add(buildValueMatcher(st2.nextToken(), MINUTE_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing minutes field: "
                        + e.getMessage() + ".");
            }
            try {
                hourMatchers.add(buildValueMatcher(st2.nextToken(), HOUR_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing hours field: "
                        + e.getMessage() + ".");
            }
            try {
                dayOfMonthMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_MONTH_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern
                        + "\". Error parsing days of month field: "
                        + e.getMessage() + ".");
            }
            try {
                monthMatchers.add(buildValueMatcher(st2.nextToken(), MONTH_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern + "\". Error parsing months field: "
                        + e.getMessage() + ".");
            }
            try {
                dayOfWeekMatchers.add(buildValueMatcher(st2.nextToken(), DAY_OF_WEEK_VALUE_PARSER));
            } catch (Exception e) {
                throw new InvalidPatternException("invalid pattern \""
                        + localPattern
                        + "\". Error parsing days of week field: "
                        + e.getMessage() + ".");
            }
            matcherSize++;
        }
    }

    /**
     * Validates a string as a scheduling pattern.
     *
     * @param schedulingPattern The pattern to validate.
     * @return true if the given string represents a valid scheduling pattern;
     * false otherwise.
     */
    public static boolean validate(String schedulingPattern) {
        try {
            new SchedulingPattern(schedulingPattern);
        } catch (InvalidPatternException e) {
            return false;
        }
        return true;
    }

    /**
     * This utility method changes an alias to an int value.
     *
     * @param value   The value.
     * @param aliases The aliases list.
     * @param offset  The offset appplied to the aliases list indices.
     * @return The parsed value.
     * @throws Exception If the expressed values doesn't match any alias.
     */
    private static int parseAlias(String value, String[] aliases, int offset)
            throws Exception {
        for (int i = 0; i < aliases.length; i++) {
            if (aliases[i].equalsIgnoreCase(value)) {
                return offset + i;
            }
        }
        throw new Exception("invalid alias \"" + value + "\"");
    }

    /**
     * A ValueMatcher utility builder.
     *
     * @param str    The pattern part for the ValueMatcher creation.
     * @param parser The parser used to parse the values.
     * @return The requested ValueMatcher.
     * @throws Exception If the supplied pattern part is not valid.
     */
    private ValueMatcher buildValueMatcher(String str, ValueParser parser)
            throws Exception {
        if (str.length() == 1 && str.equals("*")) {
            return new AlwaysTrueValueMatcher();
        }
        List<Integer> values = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            List<Integer> local;
            try {
                local = parseListElement(element, parser);
            } catch (Exception e) {
                throw new Exception("invalid field \"" + str
                        + "\", invalid element \"" + element + "\", "
                        + e.getMessage());
            }
            for (Integer value : local) {
                if (!values.contains(value)) {
                    values.add(value);
                }
            }
        }
        if (values.size() == 0) {
            throw new Exception("invalid field \"" + str + "\"");
        }
        if (parser == DAY_OF_MONTH_VALUE_PARSER) {
            return new DayOfMonthValueMatcher(values);
        } else {
            return new IntArrayValueMatcher(values);
        }
    }

    /**
     * Parses an element of a list of values of the pattern.
     *
     * @param str    The element string.
     * @param parser The parser used to parse the values.
     * @return A list of integers representing the allowed values.
     * @throws Exception If the supplied pattern part is not valid.
     */
    private List<Integer> parseListElement(String str, ValueParser parser)
            throws Exception {
        StringTokenizer st = new StringTokenizer(str, "/");
        int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        List<Integer> values;
        try {
            values = parseRange(st.nextToken(), parser);
        } catch (Exception e) {
            throw new Exception("invalid range, " + e.getMessage());
        }
        if (size == 2) {
            String dStr = st.nextToken();
            int div;
            try {
                div = Integer.parseInt(dStr);
            } catch (NumberFormatException e) {
                throw new Exception("invalid divisor \"" + dStr + "\"");
            }
            if (div < 1) {
                throw new Exception("non positive divisor \"" + div + "\"");
            }
            List<Integer> values2 = new ArrayList<>();
            for (int i = 0; i < values.size(); i += div) {
                values2.add(values.get(i));
            }
            return values2;
        } else {
            return values;
        }
    }

    /**
     * Parses a range of values.
     *
     * @param str    The range string.
     * @param parser The parser used to parse the values.
     * @return A list of integers representing the allowed values.
     * @throws Exception If the supplied pattern part is not valid.
     */
    private List<Integer> parseRange(String str, ValueParser parser)
            throws Exception {
        if (str.equals("*")) {
            int min = parser.getMinValue();
            int max = parser.getMaxValue();
            List<Integer> values = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                values.add(i);
            }
            return values;
        }
        StringTokenizer st = new StringTokenizer(str, "-");
        int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        String v1Str = st.nextToken();
        int v1;
        try {
            v1 = parser.parse(v1Str);
        } catch (Exception e) {
            throw new Exception("invalid value \"" + v1Str + "\", "
                    + e.getMessage());
        }
        if (size == 1) {
            List<Integer> values = new ArrayList<>();
            values.add(v1);
            return values;
        } else {
            String v2Str = st.nextToken();
            int v2;
            try {
                v2 = parser.parse(v2Str);
            } catch (Exception e) {
                throw new Exception("invalid value \"" + v2Str + "\", "
                        + e.getMessage());
            }
            List<Integer> values = new ArrayList<>();
            if (v1 < v2) {
                for (int i = v1; i <= v2; i++) {
                    values.add(i);
                }
            } else if (v1 > v2) {
                int min = parser.getMinValue();
                int max = parser.getMaxValue();
                for (int i = v1; i <= max; i++) {
                    values.add(i);
                }
                for (int i = min; i <= v2; i++) {
                    values.add(i);
                }
            } else {
                // v1 == v2
                values.add(v1);
            }
            return values;
        }
    }

    /**
     * This methods returns true if the given timestamp (expressed as a UNIX-era
     * millis value) matches the pattern, according to the given time zone.
     *
     * @param timezone A time zone.
     * @param millis   The timestamp, as a UNIX-era millis value.
     * @return true if the given timestamp matches the pattern.
     */
    private boolean match(TimeZone timezone, long millis) {
        GregorianCalendar gc = new GregorianCalendar(timezone);
        gc.setTimeInMillis(millis);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);

        int minute = gc.get(Calendar.MINUTE);
        int hour = gc.get(Calendar.HOUR_OF_DAY);
        int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH);
        int month = gc.get(Calendar.MONTH) + 1;
        int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK) - 1;
        int year = gc.get(Calendar.YEAR);

        for (int i = 0; i < matcherSize; i++) {
            ValueMatcher minuteMatcher = minuteMatchers.get(i);
            ValueMatcher hourMatcher = hourMatchers.get(i);
            ValueMatcher dayOfMonthMatcher = dayOfMonthMatchers.get(i);
            ValueMatcher monthMatcher = monthMatchers.get(i);
            ValueMatcher dayOfWeekMatcher = dayOfWeekMatchers.get(i);
            boolean eval = minuteMatcher.match(minute)
                    && hourMatcher.match(hour)
                    && ((dayOfMonthMatcher instanceof DayOfMonthValueMatcher) ? ((DayOfMonthValueMatcher) dayOfMonthMatcher)
                    .match(dayOfMonth, month, gc.isLeapYear(year))
                    : dayOfMonthMatcher.match(dayOfMonth))
                    && monthMatcher.match(month)
                    && dayOfWeekMatcher.match(dayOfWeek);
            if (eval) {
                return true;
            }
        }
        return false;
    }

    /**
     * This methods returns true if the given timestamp (expressed as a UNIX-era
     * millis value) matches the pattern, according to the system default time
     * zone.
     *
     * @param millis The timestamp, as a UNIX-era millis value.
     * @return true if the given timestamp matches the pattern.
     */
    public boolean match(long millis) {
        return match(TimeZone.getDefault(), millis);
    }

    /**
     * This methods returns next matching timestamp (expressed as a UNIX-era
     * millis value) according the pattern and given timestamp.
     *
     * @param timezone A time zone.
     * @param millis   The timestamp, as a UNIX-era millis value.
     * @return next matching timestamp after given timestamp, according pattern
     */
    private long next(TimeZone timezone, long millis) {
        long next = -1L;

        for (int i = 0; i < matcherSize; i++) {
            GregorianCalendar gc = new GregorianCalendar(timezone);
            gc.setTimeInMillis(millis);
            gc.set(Calendar.SECOND, 0);
            gc.set(Calendar.MILLISECOND, 0);

            ValueMatcher minuteMatcher = minuteMatchers.get(i);
            ValueMatcher hourMatcher = hourMatchers.get(i);
            ValueMatcher dayOfMonthMatcher = dayOfMonthMatchers.get(i);
            ValueMatcher monthMatcher = monthMatchers.get(i);
            ValueMatcher dayOfWeekMatcher = dayOfWeekMatchers.get(i);

            loop:
            for (; ; ) {
                int year = gc.get(Calendar.YEAR);
                boolean isLeapYear = gc.isLeapYear(year);

                for (int month = gc.get(Calendar.MONTH) + 1; month <= MONTH_MAX_VALUE; month++) {
                    if (monthMatcher.match(month)) {
                        gc.set(Calendar.MONTH, month - 1);
                        int maxDayOfMonth = DayOfMonthValueMatcher.getLastDayOfMonth(month, isLeapYear);
                        for (int dayOfMonth = gc.get(Calendar.DAY_OF_MONTH); dayOfMonth <= maxDayOfMonth; dayOfMonth++) {
                            if ((dayOfMonthMatcher instanceof DayOfMonthValueMatcher) ? ((DayOfMonthValueMatcher) dayOfMonthMatcher).match(dayOfMonth, month, isLeapYear) : dayOfMonthMatcher.match(dayOfMonth)) {
                                gc.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK) - 1;
                                if (dayOfWeekMatcher.match(dayOfWeek)) {
                                    for (int hour = gc.get(Calendar.HOUR_OF_DAY); hour <= HOUR_MAX_VALUE; hour++) {
                                        if (hourMatcher.match(hour)) {
                                            gc.set(Calendar.HOUR_OF_DAY, hour);
                                            for (int minute = gc.get(Calendar.MINUTE); minute <= MINUTE_MAX_VALUE; minute++) {
                                                if (minuteMatcher.match(minute)) {
                                                    gc.set(Calendar.MINUTE, minute);
                                                    long next0 = gc.getTimeInMillis();
                                                    if (next == -1L || next0 < next)
                                                        next = next0;
                                                    break loop;
                                                }
                                            }
                                        }
                                        gc.set(Calendar.MINUTE, MINUTE_MIN_VALUE);
                                    }
                                }
                            }
                            gc.set(Calendar.HOUR_OF_DAY, HOUR_MIN_VALUE);
                            gc.set(Calendar.MINUTE, MINUTE_MIN_VALUE);
                        }
                    }
                    gc.set(Calendar.DAY_OF_MONTH, DAY_OF_MONTH_MIN_VALUE);
                    gc.set(Calendar.HOUR_OF_DAY, HOUR_MIN_VALUE);
                    gc.set(Calendar.MINUTE, MINUTE_MIN_VALUE);
                }
                gc.set(Calendar.MONTH, MONTH_MIN_VALUE - 1);
                gc.set(Calendar.HOUR_OF_DAY, HOUR_MIN_VALUE);
                gc.set(Calendar.MINUTE, MINUTE_MIN_VALUE);
                gc.roll(Calendar.YEAR, true);
            }
        }

        return next;
    }

    /**
     * This methods returns next matching timestamp (expressed as a UNIX-era
     * millis value) according the pattern and given timestamp.
     *
     * @param millis The timestamp, as a UNIX-era millis value.
     * @return next matching timestamp after given timestamp, according pattern
     */
    public long next(long millis) {
        return next(TimeZone.getDefault(), millis);
    }

    /**
     * Returns the pattern as a string.
     *
     * @return The pattern as a string.
     */
    public String toString() {
        return asString;
    }

    /**
     * Definition for a value parser.
     */
    private interface ValueParser {

        /**
         * Attempts to parse a value.
         *
         * @param value The value.
         * @return The parsed value.
         * @throws Exception If the value can't be parsed.
         */
        int parse(String value) throws Exception;

        /**
         * Returns the minimum value accepred by the parser.
         *
         * @return The minimum value accepred by the parser.
         */
        int getMinValue();

        /**
         * Returns the maximum value accepred by the parser.
         *
         * @return The maximum value accepred by the parser.
         */
        int getMaxValue();

    }

    /**
     * <p>
     * This interface describes the ValueMatcher behavior. A ValueMatcher is an
     * object that validate an integer value against a set of rules.
     * </p>
     */
    private interface ValueMatcher {
        boolean match(int value);
    }

    /**
     * A simple value parser.
     */
    private static class SimpleValueParser implements ValueParser {

        /**
         * The minimum allowed value.
         */
        final int minValue;

        /**
         * The maximum allowed value.
         */
        final int maxValue;

        /**
         * Builds the value parser.
         *
         * @param minValue The minimum allowed value.
         * @param maxValue The maximum allowed value.
         */
        SimpleValueParser(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public int parse(String value) throws Exception {
            int i;
            try {
                i = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new Exception("invalid integer value");
            }
            if (i < minValue || i > maxValue) {
                throw new Exception("value out of range");
            }
            return i;
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

    }

    /**
     * The minutes value parser.
     */
    private static class MinuteValueParser extends SimpleValueParser {

        /**
         * Builds the value parser.
         */
        MinuteValueParser() {
            super(MINUTE_MIN_VALUE, MINUTE_MAX_VALUE);
        }

    }

    /**
     * The hours value parser.
     */
    private static class HourValueParser extends SimpleValueParser {

        /**
         * Builds the value parser.
         */
        HourValueParser() {
            super(HOUR_MIN_VALUE, HOUR_MAX_VALUE);
        }

    }

    /**
     * The days of month value parser.
     */
    private static class DayOfMonthValueParser extends SimpleValueParser {

        DayOfMonthValueParser() {
            super(DAY_OF_MONTH_MIN_VALUE, DAY_OF_MONTH_MAX_VALUE);
        }

        /**
         * Added to support last-day-of-month.
         *
         * @param value The value to be parsed
         * @return the integer day of the month or 32 for last day of the month
         * @throws Exception if the input value is invalid
         */
        public int parse(String value) throws Exception {
            if (value.equalsIgnoreCase("L")) {
                return 32;
            } else {
                return super.parse(value);
            }
        }

    }

    /**
     * The value parser for the months field.
     */
    private static class MonthValueParser extends SimpleValueParser {

        /**
         * Months aliases.
         */
        private static final String[] ALIASES = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

        /**
         * Builds the months value parser.
         */
        MonthValueParser() {
            super(MONTH_MIN_VALUE, MONTH_MAX_VALUE);
        }

        public int parse(String value) throws Exception {
            try {
                // try as a simple value
                return super.parse(value);
            } catch (Exception e) {
                // try as an alias
                return parseAlias(value, ALIASES, 1);
            }
        }

    }

    /**
     * The value parser for the months field.
     */
    private static class DayOfWeekValueParser extends SimpleValueParser {

        /**
         * Days of week aliases.
         */
        private static final String[] ALIASES = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

        /**
         * Builds the months value parser.
         */
        DayOfWeekValueParser() {
            super(DAY_OF_WEEK_MIN_VALUE, DAY_OF_WEEK_MAX_VALUE);
        }

        public int parse(String value) throws Exception {
            try {
                // try as a simple value
                return super.parse(value) % 7;
            } catch (Exception e) {
                // try as an alias
                return parseAlias(value, ALIASES, 0);
            }
        }

    }

    /**
     * This ValueMatcher always returns true!
     */
    private static class AlwaysTrueValueMatcher implements ValueMatcher {

        /**
         * Always true!
         */
        public boolean match(int value) {
            return true;
        }

    }

    /**
     * <p>
     * A ValueMatcher whose rules are in a plain array of integer values. When asked
     * to validate a value, this ValueMatcher checks if it is in the array.
     * </p>
     */
    private static class IntArrayValueMatcher implements ValueMatcher {

        /**
         * The accepted values.
         */
        private final int[] values;

        /**
         * Builds the ValueMatcher.
         *
         * @param integers An ArrayList<Integer> of Integer elements, one for every value accepted
         *                 by the matcher. The match() method will return true only if
         *                 its parameter will be one of this list.
         */
        IntArrayValueMatcher(List<Integer> integers) {
            int size = integers.size();
            values = new int[size];
            for (int i = 0; i < size; i++) {
                try {
                    values[i] = integers.get(i);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }

        /**
         * Returns true if the given value is included in the matcher list.
         */
        public boolean match(int value) {
            for (int value1 : values) {
                if (value1 == value) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * <p>
     * A ValueMatcher whose rules are in a plain array of integer values. When asked
     * to validate a value, this ValueMatcher checks if it is in the array and, if
     * not, checks whether the last-day-of-month setting applies.
     * </p>
     */
    private static class DayOfMonthValueMatcher extends IntArrayValueMatcher {

        private static final int[] lastDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        /**
         * Builds the ValueMatcher.
         *
         * @param integers An ArrayList<Integer> of Integer elements, one for every value accepted
         *                 by the matcher. The match() method will return true only if
         *                 its parameter will be one of this list or the
         *                 last-day-of-month setting applies.
         */
        DayOfMonthValueMatcher(List<Integer> integers) {
            super(integers);
        }

        static int getLastDayOfMonth(int month, boolean isLeapYear) {
            if (isLeapYear && month == 2) {
                return 29;
            } else {
                return lastDays[month - 1];
            }
        }

        static boolean isLastDayOfMonth(int value, int month, boolean isLeapYear) {
            return value == getLastDayOfMonth(month, isLeapYear);
        }

        /**
         * Returns true if the given value is included in the matcher list or the
         * last-day-of-month setting applies.
         */
        boolean match(int value, int month, boolean isLeapYear) {
            return (super.match(value) || (value > 27 && match(32) && isLastDayOfMonth(value, month, isLeapYear)));
        }
    }

    /**
     * <p>
     * This kind of exception is thrown if an invalid scheduling pattern is
     * encountered by the scheduler.
     * </p>
     */
    public class InvalidPatternException extends RuntimeException {
        InvalidPatternException(String message) {
            super(message);
        }
    }
}
