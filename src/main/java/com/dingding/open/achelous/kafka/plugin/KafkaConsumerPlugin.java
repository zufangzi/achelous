/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.util.Iterator;
import java.util.Map;

import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.plugin.PluginTypes;
import com.dingding.open.achelous.core.support.Context;

/**
 * kafka消费者的插件
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
@PluginName(PluginTypes.KAFKA_CONSUMER)
public class KafkaConsumerPlugin extends AbstractPlugin {

    @Override
    public void onError(Iterator invokers, Context context, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompleted(Iterator invokers, Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doWork(Iterator invokers, Context context, Map config) {
        // TODO Auto-generated method stub

    }

}
