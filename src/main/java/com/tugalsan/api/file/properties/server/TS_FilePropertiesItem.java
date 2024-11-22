package com.tugalsan.api.file.properties.server;

public record TS_FilePropertiesItem(CharSequence name, CharSequence value) {

    public static TS_FilePropertiesItem of(CharSequence name, CharSequence value) {
        return new TS_FilePropertiesItem(name, value);
    }
}
