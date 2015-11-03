/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.example.old;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * TODO will be deleted soon
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
public class PublishDemo {
    private static Producer<String, String> producer;
    private final static String TOPIC = "my-replicated-topic";

    public static final PublishDemo publisher = new PublishDemo();

    private PublishDemo() {
        init();
    }

    public void init() {
        Properties props = new Properties();
        // 此处配置的是kafka的端口
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.128:9092");

        // 配置value的序列化类
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // 配置key的序列化类
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "-1");

        producer = new KafkaProducer<String, String>(props);
    }

    public void produce() {
        int messageNo = 1000;
        final int COUNT = 1000000;

        while (messageNo < COUNT) {
            System.out.println("begin.................." + messageNo);
            String key = String.valueOf(messageNo);
            String data = "hello kafka message " + key;
            producer.send(new ProducerRecord<String, String>(TOPIC, key, data));
            System.out.println(data);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            messageNo++;
        }
    }

    public static void main(String[] args) {
        publisher.produce();
    }
}
