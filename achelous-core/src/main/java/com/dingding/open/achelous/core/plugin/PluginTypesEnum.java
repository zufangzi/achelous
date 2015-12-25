/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.core.plugin;

/**
 * 插件类型。如果需要扩展，也可以通过String来自定义。
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public enum PluginTypesEnum {
    /**
     * IO阻塞方式并发插件。
     */
    PARRELE_IO,

    /**
     * 以kafka作为消费者的插件。
     */
    KAFKA_CONSUMER,

    /**
     * 通用的处理器插件。
     */
    COM_PROC,

    /**
     * 以kafka作为生产者的插件。
     */
    KAFKA_PRODUCER
}
