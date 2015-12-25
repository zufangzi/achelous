/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.support;

import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.AbstractEntrance;
import com.dingding.open.achelous.core.DefaultProps;
import com.dingding.open.achelous.core.FilePath;
import com.dingding.open.achelous.core.PluginPath;
import com.dingding.open.achelous.core.support.Context;

/**
 * <pre>
 * seda框架的kafka实现入口。集成发布消息、消费消息、核心处理的功能与入口 
 * DefaultProps注解只能在应用层声明.在plugin声明是不准确的. 因为可能存在对plugin的不同用法
 * </pre>
 * 
 * @author surlymo
 * @date Oct 29, 2015
 */
@Component
@PluginPath("com.dingding.open.achelous.kafka.plugin")
@FilePath("kafka.properties")
@DefaultProps({ "async.cooker=com.dingding.open.achelous.kafka.support.KafkaAsyancCooker" })
public class AchelousKafka extends AbstractEntrance {

    public AchelousKafka() {
        super();
    }

    private AchelousKafka(boolean flag) {
        super(flag);
    }

    // 非spring调用时候使用
    public static final AchelousKafka INSTANCE = new AchelousKafka(true);

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

    public <T> void sub(String pipeline, Context context) {
        manager.call(pipeline, context);
    }

    public <T> void sub(String pipeline) {
        manager.call(pipeline, new Context());
    }

    public void sub() {
        manager.call(new Context());
    }

    private static Context fillPubBaseContext(String key, Object value) {
        Context context = new Context();
        KafkaContext kafkaContext = new KafkaContext();
        kafkaContext.setKey(key);
        kafkaContext.setValue(value);
        context.getContextMap().put("kafka", kafkaContext);
        return context;
    }
}
