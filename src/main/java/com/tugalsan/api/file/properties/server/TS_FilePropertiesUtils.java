package com.tugalsan.api.file.properties.server;

import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import com.tugalsan.api.function.client.maythrow.uncheckedexceptions.TGS_FuncMTUCEUtils;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import com.tugalsan.api.union.client.TGS_UnionExcuse;

import java.util.stream.IntStream;

public class TS_FilePropertiesUtils {

    public static Properties createNewInstance() {
        return new Properties();
    }

    public static void print(Properties config) {
        TGS_FuncMTCEUtils.run(() -> config.store(System.out, "Loaded properties:"));
    }

    public static Optional<String> getValue(Properties source, CharSequence key) {
        var result = source.getProperty(key.toString());
        return TGS_StringUtils.cmn().isNullOrEmpty(result) ? Optional.empty() : Optional.of(result);
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

    public static void removeKey(Properties source, CharSequence key) {
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

    public static List<TS_FilePropertiesItem> getAllItems(Properties source) {
        List<TS_FilePropertiesItem> items = new ArrayList();
        getAllKeys(source).forEach(name -> {
            items.add(TS_FilePropertiesItem.of(name, getValue(source, name).orElseThrow()));
        });
        return items;
    }

    public static void setAllItems(Properties source, List<TS_FilePropertiesItem> items, boolean skipIfExists) {
        items.forEach(item -> {
            if (skipIfExists) {
                var op = getValue(source, item.name());
                if (op.isPresent()) {
                    return;
                }
            }
            setValue(source, item.name(), item.value());
        });
    }

    public void toProperties(List<String> sourceKey, List<String> sourceValue, Properties dest) {
        dest.clear();
        IntStream.range(0, sourceKey.size()).forEachOrdered(i -> dest.setProperty(sourceKey.get(i), sourceValue.get(i)));
    }

    public void toProperties(List<TGS_Tuple2<String, String>> destKeyAndValues, Properties dest) {
        dest.clear();
        IntStream.range(0, destKeyAndValues.size()).forEachOrdered(i -> {
            dest.setProperty(destKeyAndValues.get(i).value0, destKeyAndValues.get(i).value1);
        });
    }

    public void toList(Properties source, List<TGS_Tuple2<String, String>> destKeyAndValues) {
        destKeyAndValues.clear();
        source.entrySet().stream().forEachOrdered(entry -> {
            destKeyAndValues.add(new TGS_Tuple2(
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

    public static TGS_UnionExcuse<Properties> read(CharSequence data) {
        return TGS_FuncMTCEUtils.call(() -> {
            var config = new Properties();
            if (data != null) {
                config.load(new StringReader(data.toString()));
            }
            return TGS_UnionExcuse.of(config);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }

    public static TGS_UnionExcuse<Properties> createPropertyReader(Class className) {
        return TGS_FuncMTCEUtils.call(() -> {
            var propsName = className.getName();//NOT SIMPLE NAME
            var name = propsName.replace('.', '/').concat(".properties");
            var cl = ClassLoader.getSystemClassLoader();
            try (var is = cl.getResourceAsStream(name)) {
                if (is == null) {
                    TGS_FuncMTUCEUtils.thrw(TS_FilePropertiesUtils.class.getSimpleName(), "createPropertyReader", "in == null");
                }
                var props = new Properties();
                props.load(is);
                return TGS_UnionExcuse.of(props);
            }
        }, e -> {
            return TGS_UnionExcuse.ofExcuse(e);
        });
    }

    public static TGS_UnionExcuse<Properties> createPropertyReader(Path file) {
        return TGS_FuncMTCEUtils.call(() -> {
            try (var is = Files.newInputStream(file)) {
                if (is == null) {
                    TGS_FuncMTUCEUtils.thrw(TS_FilePropertiesUtils.class.getSimpleName(), "createPropertyReader", "in == null");
                }
                var props = new Properties();
                props.load(is);
                return TGS_UnionExcuse.of(props);
            }
        }, e -> {
            return TGS_UnionExcuse.ofExcuse(e);
        });
    }

    public static void write(Properties source, Path dest) {
        TGS_FuncMTCEUtils.run(() -> {
            try (var os = Files.newOutputStream(dest); var osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);) {
                source.store(osw, "");
            }
        });
    }

    public static Properties read(Path source) {
        return TGS_FuncMTCEUtils.call(() -> {
            try (var is = Files.newInputStream(source); var isr = new InputStreamReader(is, StandardCharsets.UTF_8);) {
                var config = new Properties();
                config.load(isr);
                return config;
            }
        });
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
