/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.parser.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.dingding.open.achelous.core.parser.Parser;

/**
 * properties解析器
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public abstract class PropertiesParser implements Parser {

    private static final String FILENAME = "seda.properties";
    protected static final Map<String, String> keyValues = new HashMap<String, String>();

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

}
