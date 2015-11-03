/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.dingding.open.achelous.core.invoker.Invoker;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.PropertiesUtils;
import com.dingding.open.achelous.kafka.support.KafkaContext;
import com.dingding.open.achelous.kafka.support.KafkaPluginTypes;
import com.dingding.open.achelous.worker.MessageWorker;
import com.dingding.open.achelous.worker.WorkerFactory;

/**
 * kafka消费者的插件
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
@PluginName(KafkaPluginTypes.KAFKA_CONSUMER)
public class KafkaConsumerPlugin extends AbstractPlugin<KafkaContext> {

    private static final Logger logger = Logger.getLogger(KafkaConsumerPlugin.class);

    private static final ConfigConstant CONF_FROM = new ConfigConstant("from", "default_topic");
    private static final ConfigConstant CONF_ZKCONFIG = new ConfigConstant("zkconfig", "127.0.0.1:2181");
    private static final ConfigConstant CONF_GROUP = new ConfigConstant("group", "default_group");
    private static final ConfigConstant CONF_STREAMS = new ConfigConstant("streams", "1");
    private static final ConfigConstant CONF_WORKER = new ConfigConstant("worker", "");

    @Override
    public void onError(Iterator<Invoker> invokers, KafkaContext context, Throwable t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompleted(Iterator<Invoker> invokers, KafkaContext context) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void doWork(Iterator<Invoker> invokers, final KafkaContext context, Map<String, String> config) {
        if (exhaust()) {
            Properties props = new Properties();
            PropertiesUtils.put(props, "zookeeper.connect", CONF_ZKCONFIG, config);
            PropertiesUtils.put(props, "group.id", CONF_GROUP, config);
            props.put("zookeeper.session.timeout.ms", "400");
            props.put("zookeeper.sync.time.ms", "200");
            props.put("auto.commit.interval.ms", "1000");

            ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
            String topic = config.get(CONF_FROM.getName());
            int threads = 0;
            if (config.get(CONF_STREAMS.getName()) != null) {
                threads = Integer.valueOf(config.get(CONF_STREAMS.getName()));
            } else {
                threads = Integer.valueOf(CONF_STREAMS.getDefaultConfig());
            }

            try {
                final MessageWorker worker = WorkerFactory.getWorker(config.get(CONF_WORKER.getName()).toString());

                Type genType = worker.getClass().getGenericInterfaces()[0];
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                final Class<?> clazz = (Class<?>) params[0];

                Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
                topicCountMap.put(topic, threads);
                Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                        consumer.createMessageStreams(topicCountMap);
                List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

                ExecutorService executor = Executors.newFixedThreadPool(threads);

                final AtomicInteger threadNumber = new AtomicInteger(0);
                for (final KafkaStream<byte[], byte[]> stream : streams) {
                    System.out.println(stream.toString());

                    executor.execute(new Runnable() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public void run() {
                            logger.info("Begin to run Thread: " + threadNumber);
                            ConsumerIterator<byte[], byte[]> it = stream.iterator();
                            while (it.hasNext()) {
                                MessageAndMetadata<byte[], byte[]> data = it.next();
                                if (clazz.isAssignableFrom(String.class)) {
                                    worker.proc(new String(data.message()));
                                } else if (clazz.isAssignableFrom(Integer.class)) {
                                    worker.proc(Integer.valueOf(new String(data.message())));
                                } else {
                                    worker.proc(JSON.parseObject(data.message(), clazz));
                                }
                            }

                            logger.info("Shutting down Thread: " + threadNumber);
                        }

                    });
                    System.out.println(threadNumber);
                    threadNumber.incrementAndGet();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            logger.info("[ACHELOUS]kafka consumer plugin finish initialization");
        }
    }

}
