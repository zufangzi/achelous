/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.dingding.open.achelous.core.Factory;
import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;
import com.dingding.open.achelous.kafka.support.KafkaContext;
import com.dingding.open.achelous.kafka.support.KafkaPluginTypes;
import com.dingding.open.achelous.worker.MessageWorker;

/**
 * kafka通用消费者处理插件
 * 
 * @author surlymo
 * @date Nov 10, 2015
 */
@PluginName(KafkaPluginTypes.KAFKA_PROC)
public class KafkaComProcessorPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(KafkaComProcessorPlugin.class);

    private static final ConfigConstant CONF_WORKER = new ConfigConstant("worker", "");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object doWork(InvokerCore core, final Context context, final Map<String, String> config)
            throws Throwable {

        MessageWorker worker = Factory.getEntity(config.get(CONF_WORKER.getName()).toString());

        Type genType = worker.getClass().getGenericInterfaces()[0];
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        final Class<?> clazz = (Class<?>) params[0];

        KafkaStream<byte[], byte[]> stream = (KafkaStream<byte[], byte[]>) context.getResult().get();
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> data = it.next();
            KafkaContext kafkaContext = null;
            if (clazz.isAssignableFrom(String.class)) {
                kafkaContext = (KafkaContext) worker.proc(new String(data.message()));
            } else if (clazz.isAssignableFrom(Integer.class)) {
                kafkaContext = (KafkaContext) worker.proc(Integer.valueOf(new String(data.message())));
            } else {
                kafkaContext = (KafkaContext) worker.proc(JSON.parseObject(data.message(), clazz));
            }
            context.getContextMap().put("kafka", kafkaContext);

            if (core.getCurrentIndex().get() < core.getInvokers().size() - 1) {
                core.nextNFromM(core.getCurrentIndex().get(), 1, false).invoke(core);
            } else {
                logger.info("[ACHELOUS]not next plugin");
            }
        }
        logger.info("[ACHELOUS]Shutting down Thread: " + Thread.currentThread().getName());
        return null;
    }

}
