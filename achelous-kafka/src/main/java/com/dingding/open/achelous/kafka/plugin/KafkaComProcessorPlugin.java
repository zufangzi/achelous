/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

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
    public Object doWork(InvokerCore core, final Context context, final Map<String, String> config) throws Throwable {

        MessageWorker worker = Factory.getEntity(config.get(CONF_WORKER.getName()).toString());

        Type genType = worker.getClass().getGenericInterfaces()[0];
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        final Class<?> clazz = (Class<?>) params[0];

        KafkaStream<byte[], byte[]> stream = (KafkaStream<byte[], byte[]>) context.getResult().get();
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        int current = core.getCurrentIndex().get();
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> data = it.next();
            List<KafkaContext> kafkaContexts = null;
            if (clazz.isAssignableFrom(String.class)) {
                kafkaContexts = (List<KafkaContext>) worker.proc(new String(data.message()));
            } else if (clazz.isAssignableFrom(Integer.class)) {
                kafkaContexts = (List<KafkaContext>) worker.proc(Integer.valueOf(new String(data.message())));
            } else {
                System.out.println(new String(data.message()));
                kafkaContexts = (List<KafkaContext>) worker.proc(JSON.parseObject(data.message(), clazz));
            }

            if (kafkaContexts != null) {
                for (KafkaContext meta : kafkaContexts) {
                    context.getContextMap().put("kafka", meta);
                    for (int tmpCurrent = current; tmpCurrent < core.getInvokers().size() - 1; tmpCurrent++) {
                        System.out.println("now in.....");
                        core.nextNFromM(tmpCurrent, 1, false).invoke(core);
                    }
                    logger.info("[ACHELOUS]not next plugin");
                }
            }
        }
        logger.info("[ACHELOUS]Shutting down Thread: " + Thread.currentThread().getName());
        return null;
    }

}
