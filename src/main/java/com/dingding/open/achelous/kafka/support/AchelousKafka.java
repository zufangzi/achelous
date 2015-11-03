/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.support;

import com.dingding.open.achelous.core.AbstractEntrance;
import com.dingding.open.achelous.core.PipelineManager;

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
        super("com.dingding.open.achelous.kafka.plugin");
    }

    public void init() {
        logger.info("[ACHELOUS]initialization finished...");
    }

    public void pub(Object value) {
        manager.call(fillPubBaseContext("", value));
    }

    /**
     * 发送消息
     * 
     * @param key 发送的key
     * @param value 发送的value
     */
    public void pubDefaultKey(String pipeline, Object value) {
        manager.call(pipeline, fillPubBaseContext("", value));
    }

    /**
     * 发送消息
     * 
     * @param pipeline 发送选择的套件，缺省找第一个
     * @param key 发送的key
     * @param value 发送的value
     */
    public void pubDefaultPipe(String key, Object value) {
        manager.call(fillPubBaseContext(key, value));
    }

    /**
     * 发送消息
     * 
     * @param key 发送的key
     * @param value 发送的value
     */
    public <T> void pubFull(String pipeline, String key, T value) {
        manager.call(pipeline, fillPubBaseContext(key, value));
    }

    public <T> void sub(String pipeline, KafkaContext context) {
        manager.call(pipeline, context);
    }

    public <T> void sub(String pipeline) {
        manager.call(pipeline, new KafkaContext());
    }

    public void sub() {
        manager.call(new KafkaContext());
    }

    private static KafkaContext fillPubBaseContext(String key, Object value) {
        KafkaContext context = new KafkaContext();
        context.setKey(key);
        context.setValue(value);
        return context;
    }
}
