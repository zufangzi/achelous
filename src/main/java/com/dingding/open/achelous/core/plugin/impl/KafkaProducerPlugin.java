package com.dingding.open.achelous.core.plugin.impl;

import java.util.Iterator;

import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.Plugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.plugin.PluginTypes;
import com.dingding.open.achelous.core.support.Context;

@PluginName(PluginTypes.KAFKA_PRODUCER)
public class KafkaProducerPlugin implements Plugin {

    @Override
    public void onNext(Iterator<Invoker> invokers, Context context) {
        System.out.println("kafka producer...");
    }

    @Override
    public void onError(Iterator<Invoker> invokers, Context context, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompleted(Iterator<Invoker> invokers, Context context) {
        // TODO Auto-generated method stub

    }

}
