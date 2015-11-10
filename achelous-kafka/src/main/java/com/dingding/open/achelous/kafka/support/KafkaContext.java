/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.support;

/**
 * plugin执行过程中的上下文封装。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class KafkaContext {
    private String key;
    private Object value;

    public KafkaContext() {

    }

    public KafkaContext(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
