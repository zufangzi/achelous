/**
 * DingDing.com Inc. Copyright (c) 2000-2015 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.benchmark;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dingding.open.achelous.kafka.KafkaBootStraper;

public class KafkaAndAchelousBenchmarkTest {

    static int num = 2000000;

    @Test
    public void test_achelous_sub() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:ctx-sal.xml");
        KafkaBootStraper.startSpringConsumer();

        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_achelous_send() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:ctx-sal.xml");

        final AgentKpDetailsData data = new AgentKpDetailsData();
        data.setProductIds(Arrays.asList(8812312L));
        data.setReason("noreason2");
        data.setValid(2);

        final AtomicInteger cnt = new AtomicInteger(0);
        KafkaBootStraper.get().pub(data);

        System.out.println("===============\n\n\n");
        ExecutorService executor = Executors.newFixedThreadPool(1000);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {

            executor.execute(new Runnable() {

                @Override
                public void run() {
                    KafkaBootStraper.get().pub(data);
                    cnt.incrementAndGet();
                }
            });
        }

        while (cnt.get() < num) {

        }

        long end = System.currentTimeMillis();

        System.out.println("COST: " + (end - begin));
        System.gc();
        // System.in.read();
    }

    @Test
    public void test_common_send() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:ctx-sal.xml");

        final AgentKpDetailsData data = new AgentKpDetailsData();
        data.setProductIds(Arrays.asList(8812312L));
        data.setReason("noreason2");
        data.setValid(2);
        System.out.println("bytes length: " + data.toString().getBytes().length);
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "com.dingding.open.achelous.kafka.serialize.FastJsonSerializer");
        props.put("acks", "-1");
        props.put("bootstrap.servers", "127.0.0.1:9092");
        final KafkaProducer producer = new KafkaProducer(props);

        final AtomicInteger cnt = new AtomicInteger(0);

        Executor executor = Executors.newFixedThreadPool(1000);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {

            executor.execute(new Runnable() {

                @Override
                public void run() {
                    producer.send(new ProducerRecord("my-replicated-topic", "", data));
                    cnt.incrementAndGet();
                }
            });
        }

        while (cnt.get() < num) {

        }

        long end = System.currentTimeMillis();

        System.out.println("COST: " + (end - begin));
        System.gc();
        // System.in.read();
    }

}
