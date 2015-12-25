/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.example.old;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * TODO will be deleted soon
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class ConsumerDemo {
    private static KafkaConsumer consumer;

    public static final ConsumerDemo CONSUMER_DEMO = new ConsumerDemo();

    private ConsumerDemo() {
        Properties props = new Properties();
        // zookeeper 配置
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "cc-group");

        // group 代表一个消费组
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.128:9092");

        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "smallest");
        // 序列化类
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY,
                "roundrobin");
        consumer = new KafkaConsumer(props);
    }

    void consume() {
        consumer.subscribe("test");

        Object value = null;
        System.out.println("now in...");
        while (true) {
            if ((value = consumer.poll(30000L)) != null) {
                System.out.println(value);
            }
            consumer.commit(true);
            System.out.println("not data consume...");
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        CONSUMER_DEMO.consume();
    }
}
