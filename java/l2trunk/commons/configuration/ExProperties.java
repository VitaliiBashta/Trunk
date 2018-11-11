package l2trunk.commons.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ExProperties extends Properties {
    public static final String defaultDelimiter = "[\\s,;]+";
    private static final long serialVersionUID = 1L;
    private static final List<String> True = Arrays.asList("y", "yes", "true", "1");
    private static final List<String> False = Arrays.asList("n", "no", "false", "0");

    public static boolean parseBoolean(String s) {
        if (True.contains(s.toLowerCase())) return true;
        if (False.contains(s.toLowerCase())) return false;
        throw new IllegalArgumentException("For input string: \"" + s + "\"");
    }

    public void load(Path file) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
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

    private String[] getProperty(String name, String[] defaultValue, String delimiter) {
        String[] val = defaultValue;
        String value;

        if ((value = super.getProperty(name, null)) != null)
            val = value.split(delimiter);

        return val;
    }

    public boolean[] getProperty(String name, boolean[] defaultValue) {
        return getProperty(name, defaultValue, defaultDelimiter);
    }

    private boolean[] getProperty(String name, boolean[] defaultValue, String delimiter) {
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

    private int[] getProperty(String name, int[] defaultValue, String delimiter) {
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

    private long[] getProperty(String name, long[] defaultValue, String delimiter) {
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

    private double[] getProperty(String name, double[] defaultValue, String delimiter) {
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