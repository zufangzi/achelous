/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.support;

import com.dingding.open.achelous.core.plugin.PluginTypes;

/**
 * 插件类型。如果需要扩展，也可以通过String来自定义。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class KafkaPluginTypes extends PluginTypes {

    public static final String KAFKA_PROC = "kafka_proc";

    /**
     * 以kafka作为消费者的插件。
     */
    public static final String KAFKA_CONSUMER = "kafka_consumer";

    /**
     * 以kafka作为生产者的插件。
     */
    public static final String KAFKA_PRODUCER = "kafka_producer";
}
