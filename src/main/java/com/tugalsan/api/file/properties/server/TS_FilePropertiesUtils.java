package com.tugalsan.api.file.properties.server;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_FilePropertiesUtils {

//    final private static TS_Log d = TS_Log.of(TS_FilePropertiesUtils.class.getSimpleName());
    public static Properties createPropertyReader(Class className) {
        return TGS_UnSafe.compile(() -> {
            var propsName = className.getName();//NOT SIMPLE NAME
            var name = propsName.replace('.', '/').concat(".properties");
            var cl = ClassLoader.getSystemClassLoader();
            var in = cl.getResourceAsStream(name);
            if (in == null) {
                TGS_UnSafe.catchMeIfUCan(TS_FilePropertiesUtils.class.getSimpleName(), "createPropertyReader", "in == null");
            }
            var props = new Properties();
            props.load(in);
            return props;
        });
    }

    public static void print(Properties config) {
        TGS_UnSafe.execute(() -> config.store(System.out, "Loaded properties:"));
    }

    public static String getValue(Properties source, CharSequence key) {
        return source.getProperty(key.toString());
    }

    public static String getValue(Properties source, CharSequence key, CharSequence defaultValue) {
        return defaultValue == null ? source.getProperty(key.toString()) : source.getProperty(key.toString(), defaultValue.toString());
    }

    public static void setValue(Properties source, CharSequence key, CharSequence value) {
        if (value == null) {
            source.remove(key.toString());
            return;
        }
        source.setProperty(key.toString(), value.toString());
    }

    public static List<String> getAllKeys(Properties source) {
        return TGS_StreamUtils.toList(
                source.keySet().stream().map(k -> k.toString())
        );
    }

    public void toProperties(List<String> sourceKey, List<String> sourceValue, Properties dest) {
        dest.clear();
        IntStream.range(0, sourceKey.size()).forEachOrdered(i -> dest.setProperty(sourceKey.get(i), sourceValue.get(i)));
    }

    public void toProperties(List<TGS_Pack2<String, String>> destKeyAndValues, Properties dest) {
        dest.clear();
        IntStream.range(0, destKeyAndValues.size()).forEachOrdered(i -> {
            dest.setProperty(destKeyAndValues.get(i).value0, destKeyAndValues.get(i).value1);
        });
    }

    public void toList(Properties source, List<TGS_Pack2<String, String>> destKeyAndValues) {
        destKeyAndValues.clear();
        source.entrySet().stream().forEachOrdered(entry -> {
            destKeyAndValues.add(new TGS_Pack2(
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

    public static void write(Properties source, Path dest) {
        TGS_UnSafe.execute(() -> {
            try ( var os = Files.newOutputStream(dest);  var osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);) {
                source.store(osw, "");
            }
        });
    }

    public static Properties read(Path source) {
        return TGS_UnSafe.compile(() -> {
            try ( var is = Files.newInputStream(source);  var isr = new InputStreamReader(is, StandardCharsets.UTF_8);) {
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
