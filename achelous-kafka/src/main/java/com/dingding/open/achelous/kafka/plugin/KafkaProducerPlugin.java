/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.CallbackType;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;
import com.dingding.open.achelous.kafka.support.KafkaContext;
import com.dingding.open.achelous.kafka.support.KafkaPluginTypes;

/**
 * kafka生产者的插件
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
@Component
@PluginName(KafkaPluginTypes.KAFKA_PRODUCER)
public class KafkaProducerPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(KafkaProducerPlugin.class);

    private static final ConfigConstant CONF_TOPIC = new ConfigConstant("to", "my-replicated-topic");

    private static final String CACHE_PRODUCER = "producer";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object doWork(InvokerCore core, Context context, Map<String, String> config) throws Throwable {
        // need unique msgid.
        // logger.info("[ACHELOUS]kafka producer plugin begin to process data...");
        String topic = config.get(CONF_TOPIC.getName());
        KafkaProducer producer = getCache(CacheLevel.PIPELINE_PLUGIN, CACHE_PRODUCER);

        KafkaContext realContext = (KafkaContext) context.getContextMap().get("kafka");

        // 容错策略由Failover plugin接管
        try {
            producer.send(new ProducerRecord(topic, realContext.getKey(), realContext.getValue())).get();
        } catch (Throwable t) {
            logger.error("[ACHELOUS]kafka producer found catchable exception.", t);
            throw t;
        }
        return null;
    }

    @Override
    public void onCallBack(CallbackType type, InvokerCore core, Context context) {
        switch (type) {
            case ERROR:
                logger.error("[ACHELOUS]error occur, now begin to log message into db.");
                break;

            default:
                break;
        }
    }

}
