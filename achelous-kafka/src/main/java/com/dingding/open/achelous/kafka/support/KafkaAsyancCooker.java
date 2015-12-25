/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dingding.open.achelous.core.support.ConcurrentCooker;
import com.dingding.open.achelous.core.support.ConfigConstant;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaAsyancCooker implements
        ConcurrentCooker<ConsumerConnector, Integer, Map<String, String>, List<KafkaStream<byte[], byte[]>>> {

    private static final Logger logger = Logger.getLogger(KafkaAsyancCooker.class);

    private static final ConfigConstant CONF_FROM = new ConfigConstant("from", "default_topic");

    @Override
    public List<KafkaStream<byte[], byte[]>> call(ConsumerConnector consumer, Integer threads,
            Map<String, String> config) {
        logger.info("[ACHELOUS] now in kafka async cooker");
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        String topic = config.get(CONF_FROM.getName());
        topicCountMap.put(topic, threads);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        return streams;
    }
}
