/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.log4j.Logger;

import rx.functions.Func2;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;
import com.dingding.open.achelous.core.support.PropertiesUtils;
import com.dingding.open.achelous.kafka.support.KafkaPluginTypes;

/**
 * kafka消费者的插件
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
@PluginName(KafkaPluginTypes.KAFKA_CONSUMER)
public class KafkaConsumerPlugin extends AbstractPlugin {

    private static final Logger logger = Logger.getLogger(KafkaConsumerPlugin.class);

    private static final ConfigConstant CONF_FROM = new ConfigConstant("from", "default_topic");
    private static final ConfigConstant CONF_ZKCONFIG = new ConfigConstant("zkconfig", "127.0.0.1:2181");
    private static final ConfigConstant CONF_GROUP = new ConfigConstant("group", "default_group");

    @Override
    public Object doWork(InvokerCore core, final Context context, final Map<String, String> config)
            throws Throwable {
        if (!exhaust()) {
            return null;
        }
        Properties props = new Properties();
        PropertiesUtils.put(props, "zookeeper.connect", CONF_ZKCONFIG, config);
        PropertiesUtils.put(props, "group.id", CONF_GROUP, config);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

        try {

            // 首先，将map函数写好。然后塞入
            Func2<ConsumerConnector, Integer, List<KafkaStream<byte[], byte[]>>> func2 =
                    new Func2<ConsumerConnector, Integer, List<KafkaStream<byte[], byte[]>>>() {

                        @Override
                        public List<KafkaStream<byte[], byte[]>> call(ConsumerConnector consumer, Integer threads) {
                            Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
                            String topic = config.get(CONF_FROM.getName());
                            topicCountMap.put(topic, threads);
                            Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                                    consumer.createMessageStreams(topicCountMap);
                            List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
                            return streams;
                        }
                    };

            // 接下去封装result
            context.setResultCooker(func2);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        logger.info("[ACHELOUS]kafka consumer plugin finish initialization");
        return consumer;
    }

}
