package l2f.commons.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ExProperties extends Properties {
    public static final String defaultDelimiter = "[\\s,;]+";
    private static final long serialVersionUID = 1L;

    public static boolean parseBoolean(String s) {
        boolean result;
        switch (s.length()) {
            case 1: {
                result = s.equalsIgnoreCase("y") || s.equals("1");
                return result;
            }
            case 2: {
                result = s.equalsIgnoreCase("no");
                return !result;
            }
            case 3: {
                result = s.equalsIgnoreCase("yes");
                return result;
            }
            case 4: {
                result = s.equalsIgnoreCase("true");
                return result;
                            }
            case 5: {
                result = s.equalsIgnoreCase("false");
                return !result;
            }
        }
        throw new IllegalArgumentException("For input string: \"" + s + "\"");
    }

    public void load(String fileName) throws IOException {
        load(new File(fileName));
    }

    public void load(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            load(is);
        }
    }

    public boolean getProperty(String name, boolean defaultValue) {
        boolean val = defaultValue;

        String value;

        if ((value = super.getProperty(name, null)) != null)
            val = parseBoolean(value);

        return val;
    }

    public int getProperty(String name, int defaultValue) {
        int val = defaultValue;

        String value;

        if ((value = super.getProperty(name, null)) != null)
            val = Integer.parseInt(value);

        return val;
    }

    public long getProperty(String name, long defaultValue) {
        long val = defaultValue;

        String value;

        if ((value = super.getProperty(name, null)) != null)
            val = Long.parseLong(value);

        return val;
    }

    public double getProperty(String name, double defaultValue) {
        double val = defaultValue;

        String value;

        if ((value = super.getProperty(name, null)) != null)
            val = Double.parseDouble(value);

        return val;
    }

    public String[] getProperty(String name, String[] defaultValue) {
        return getProperty(name, defaultValue, defaultDelimiter);
    }

    public String[] getProperty(String name, String[] defaultValue, String delimiter) {
        String[] val = defaultValue;
        String value;

        if ((value = super.getProperty(name, null)) != null)
            val = value.split(delimiter);

        return val;
    }

    public boolean[] getProperty(String name, boolean[] defaultValue) {
        return getProperty(name, defaultValue, defaultDelimiter);
    }

    public boolean[] getProperty(String name, boolean[] defaultValue, String delimiter) {
        boolean[] val = defaultValue;
        String value;

        if ((value = super.getProperty(name, null)) != null) {
            String[] values = value.split(delimiter);
            val = new boolean[values.length];
            for (int i = 0; i < val.length; i++)
                val[i] = parseBoolean(values[i]);
        }

        return val;
    }

    public int[] getProperty(String name, int[] defaultValue) {
        return getProperty(name, defaultValue, defaultDelimiter);
    }

    public int[] getProperty(String name, int[] defaultValue, String delimiter) {
        int[] val = defaultValue;
        String value;

        if ((value = super.getProperty(name, null)) != null) {
            String[] values = value.split(delimiter);
            val = new int[values.length];
            for (int i = 0; i < val.length; i++)
                val[i] = Integer.parseInt(values[i]);
        }

        return val;
    }

    public long[] getProperty(String name, long[] defaultValue) {
        return getProperty(name, defaultValue, defaultDelimiter);
    }

    public long[] getProperty(String name, long[] defaultValue, String delimiter) {
        long[] val = defaultValue;
        String value;

        if ((value = super.getProperty(name, null)) != null) {
            String[] values = value.split(delimiter);
            val = new long[values.length];
            for (int i = 0; i < val.length; i++)
                val[i] = Long.parseLong(values[i]);
        }

        return val;
    }

    public double[] getProperty(String name, double[] defaultValue) {
        return getProperty(name, defaultValue, defaultDelimiter);
    }

    public double[] getProperty(String name, double[] defaultValue, String delimiter) {
        double[] val = defaultValue;
        String value;

        if ((value = super.getProperty(name, null)) != null) {
            String[] values = value.split(delimiter);
            val = new double[values.length];
            for (int i = 0; i < val.length; i++)
                val[i] = Double.parseDouble(values[i]);
        }

        return val;
    }
}