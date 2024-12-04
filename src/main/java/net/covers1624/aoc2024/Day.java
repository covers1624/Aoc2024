package net.covers1624.aoc2024;

import net.covers1624.quack.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

// Args for dev: -f 0 -wi 0 -i 1 -r 0 -bs 1
public class Day {

    public final Logger LOGGER = LogManager.getLogger(getClass());

    public String load(String file) {
        return new String(bytes(file), StandardCharsets.UTF_8);
    }

    public List<String> loadLines(String file) {
        try {
            return IOUtils.readAll(bytes(file));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] bytes(String file) {
        try (InputStream is = open(file)) {
            return IOUtils.toBytes(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public InputStream open(String file) {
        return Day.class.getResourceAsStream("/" + getClassName(getClass()).toLowerCase(Locale.ROOT) + "/" + file);
    }

    public void assertResult(long result, long expected) {
        if (result != expected) {
            throw new RuntimeException("Expected " + expected + " got " + result);
        }
    }

    private static String getClassName(Class<?> clazz) {
        while (clazz.getName().contains("_jmhType")) {
            clazz = clazz.getSuperclass();
        }
        return clazz.getSimpleName();
    }
}
