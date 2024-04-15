package com.tugalsan.api.file.properties.server;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;

public class TS_FilePropertiesUtils {

    public static Properties empty() {
        return new Properties();
    }

    public static TGS_UnionExcuse<Properties> createPropertyReader(Class className) {
        var propsName = className.getName();//NOT SIMPLE NAME
        var name = propsName.replace('.', '/').concat(".properties");
        var cl = ClassLoader.getSystemClassLoader();
        try (var is = cl.getResourceAsStream(name)) {
            if (is == null) {
                TGS_UnionExcuse.ofExcuse(TS_FilePropertiesUtils.class.getSimpleName(), "createPropertyReader", "in == null");
            }
            var props = new Properties();
            props.load(is);
            return TGS_UnionExcuse.of(props);
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<Properties> createPropertyReader(Path file) {
        try (var is = Files.newInputStream(file)) {
            if (is == null) {
                TGS_UnionExcuse.ofExcuse(TS_FilePropertiesUtils.class.getSimpleName(), "createPropertyReader", "in == null");
            }
            var props = new Properties();
            props.load(is);
            return TGS_UnionExcuse.of(props);
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuseVoid print(Properties config) {
        try {
            config.store(System.out, "Loaded properties:");
            return TGS_UnionExcuseVoid.ofVoid();
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static Optional<String> getValue(Properties source, CharSequence key) {
        var result = source.getProperty(key.toString());
        return TGS_StringUtils.isNullOrEmpty(result) ? Optional.empty() : Optional.of(result);
    }

    public static String getValue(Properties source, CharSequence key, CharSequence defaultValue) {
        if (source == null || key == null) {
            return null;
        }
        var keyStr = key.toString();
        var val = source.getProperty(keyStr);
        if (val != null) {
            return val;
        }
        if (defaultValue == null) {
            return null;
        }
        var defStr = defaultValue.toString();
        source.setProperty(key.toString(), defStr);
        return defStr;
    }

    public static void removeValue(Properties source, CharSequence key) {
        setValue(source, key, null);
    }

    public static void setValue(Properties source, CharSequence key, Object value) {
        if (value == null) {
            source.remove(key.toString());
            return;
        }
        source.setProperty(key.toString(), value.toString());
    }

    public static List<String> getAllKeys(Properties source) {
        return TGS_StreamUtils.toLst(
                source.keySet().stream().map(k -> k.toString())
        );
    }

    public void toProperties(List<String> sourceKey, List<String> sourceValue, Properties dest) {
        dest.clear();
        IntStream.range(0, sourceKey.size()).forEachOrdered(i -> dest.setProperty(sourceKey.get(i), sourceValue.get(i)));
    }

    public void toProperties(List<TS_FilePropertiesParam> destKeyAndValues, Properties dest) {
        dest.clear();
        IntStream.range(0, destKeyAndValues.size()).forEachOrdered(i -> {
            dest.setProperty(destKeyAndValues.get(i).name(), destKeyAndValues.get(i).value());
        });
    }

    public void toList(Properties source, List<TS_FilePropertiesParam> destKeyAndValues) {
        destKeyAndValues.clear();
        source.entrySet().stream().forEachOrdered(entry -> {
            destKeyAndValues.add(new TS_FilePropertiesParam(
                    String.valueOf(entry.getKey()),
                    String.valueOf(entry.getValue())
            ));
        });
    }

    public void toList(Properties source, List<String> destKey, List<String> destValue) {
        destKey.clear();
        destValue.clear();
        source.entrySet().stream().forEachOrdered(entry -> {
            destKey.add(String.valueOf(entry.getKey()));
            destValue.add(String.valueOf(entry.getValue()));
        });
    }

    public static void clear(Properties source) {
        source.clear();
    }

    public static TGS_UnionExcuseVoid write(Properties source, Path dest) {
        try (var os = Files.newOutputStream(dest); var osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);) {
            source.store(osw, "");
            return TGS_UnionExcuseVoid.ofVoid();
        } catch (IOException ex) {
            return TGS_UnionExcuseVoid.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<Properties> read(CharSequence data) {
        try {
            var config = new Properties();
            if (data != null) {
                config.load(new StringReader(data.toString()));
            }
            return TGS_UnionExcuse.of(config);
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    public static TGS_UnionExcuse<Properties> read(Path source) {
        try (var is = Files.newInputStream(source); var isr = new InputStreamReader(is, StandardCharsets.UTF_8);) {
            var config = new Properties();
            config.load(isr);
            return TGS_UnionExcuse.of(config);
        } catch (IOException ex) {
            return TGS_UnionExcuse.ofExcuse(ex);
        }
    }

    @Deprecated
    public static void internationalize() {
        //https://docs.oracle.com/javase/tutorial/i18n/intro/steps.html
//        aLocale = new Locale("en","US");
//        currentLocale = new Locale(language, country);
//        messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
//        MessagesBundle_en_US.properties
    }
}
