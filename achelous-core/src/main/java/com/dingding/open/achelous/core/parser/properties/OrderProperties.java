/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.parser.properties;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 按照顺序读取的properties解析器
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class OrderProperties extends Properties {

    private static final long serialVersionUID = 1L;
    private final Set<Object> keys = new LinkedHashSet<Object>();

    @Override
    public Object put(Object key, Object value) {
        keys.add(new SortedUnuniqueKey(key, value));
        return super.put(key, value);
    }

    @Override
    public Set<Object> keySet() {
        return keys;
    }

    class SortedUnuniqueKey {
        private Object key;
        private Object value;

        public SortedUnuniqueKey(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public void setKey(Object key) {
            this.key = key;
        }

    }

}