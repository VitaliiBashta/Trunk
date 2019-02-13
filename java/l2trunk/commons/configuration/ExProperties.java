package l2trunk.commons.configuration;

import l2trunk.commons.lang.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public final class ExProperties extends Properties {
    public static final String defaultDelimiter = "[\\s,;]+";
    private static final Logger _log = LoggerFactory.getLogger(ExProperties.class);
    private static final List<String> True = List.of("y", "yes", "true", "1");
    private static final List<String> False = List.of("n", "no", "false", "0");

    public static boolean parseBoolean(String s) {
        if (True.contains(s.toLowerCase())) return true;
        if (False.contains(s.toLowerCase())) return false;
        throw new IllegalArgumentException("For input string: \"" + s + "\"");
    }

    public void load(Path file) {
        try (InputStream is = Files.newInputStream(file)) {
            load(is);
        } catch (IOException e) {
            _log.warn("Error loading config : " + file.toString() + "!");
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

    public List<Integer> getProperty(String name, List<Integer> defaultValue) {
        String value;

        if ((value = super.getProperty(name, null)) != null) {
            List<String> values = List.of(value.split(defaultDelimiter));
            return values.stream()
                    .map(NumberUtils::toInt)
                    .collect(Collectors.toList());
        }
        return defaultValue;
    }

}