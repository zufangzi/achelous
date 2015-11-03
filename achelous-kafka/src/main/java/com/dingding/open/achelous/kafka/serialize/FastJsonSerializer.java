/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.serialize;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.alibaba.fastjson.JSON;

/**
 * fastjson序列化反序列化工具
 * 
 * @author surlymo
 * @date Nov 3, 2015
 */
public class FastJsonSerializer<T> implements Serializer<T> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] serialize(String topic, T data) {
        return JSON.toJSONBytes(data);
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
