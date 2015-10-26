package com.dingding.open.achelous.core.parser.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.dingding.open.achelous.core.parser.Parser;

public abstract class PropertiesParser implements Parser {

    private static final String FILENAME = "seda.properties";
    protected static final Map<String, String> keyValues = new HashMap<String, String>();

    // protected static final Map<>

    static {
        Properties prop = new Properties();
        InputStream input = ClassLoader.getSystemResourceAsStream(FILENAME);
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Object initKey : prop.keySet()) {
            String key = (String) initKey;
            String value = prop.getProperty(key);
            keyValues.put(key, value);
        }
    }

    // protected Map<String, String> getProperties() {
    // // 首先，将properties中的所有key中找出第一列，即所有的实例，按实例分组。
    //
    // }
}
