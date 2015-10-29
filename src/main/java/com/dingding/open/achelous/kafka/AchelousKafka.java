/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka;

import com.dingding.open.achelous.core.AbstractEntrance;
import com.dingding.open.achelous.core.PipelineManager;
import com.dingding.open.achelous.core.support.KafkaContext;

/**
 * seda框架的kafka实现入口。集成发布消息、消费消息、核心处理的功能与入口
 * 
 * @author surlymo
 * @date Oct 29, 2015
 */
public class AchelousKafka extends AbstractEntrance {
    private static final PipelineManager manager = new PipelineManager();

    public static final AchelousKafka INSTANCE = new AchelousKafka();

    private AchelousKafka() {
        super();
    }

    /**
     * 发送消息
     * 
     * @param key 发送的key
     * @param value 发送的value
     */
    public void pub(String pipeline, String key, Object value) {
        manager.call(pipeline, fillBaseContext(key, value));
    }

    /**
     * 发送消息
     * 
     * @param pipeline 发送选择的套件，缺省找第一个
     * @param key 发送的key
     * @param value 发送的value
     */
    public void pub(String key, Object value) {
        manager.call(fillBaseContext(key, value));
    }

    private static KafkaContext<String, Object> fillBaseContext(String key, Object value) {
        KafkaContext<String, Object> context = new KafkaContext<String, Object>();
        context.setKey(key);
        context.setValue(value);
        return context;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            // pub("msgCenter", "hello", "kitty" + i);
        }
    }

    @Override
    protected String getPluginPath() {
        return "com.dingding.open.achelous.kafka.plugin";
    }
}
