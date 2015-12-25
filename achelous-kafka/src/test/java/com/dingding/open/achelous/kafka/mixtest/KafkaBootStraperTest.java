/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.mixtest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dingding.open.achelous.kafka.KafkaBootStraper;
import com.dingding.open.achelous.kafka.TestObj;

/**
 * kafka入口测试
 * 
 * @author surlymo
 * @date Oct 29, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:achelous-core.xml")
public class KafkaBootStraperTest {

    @Test
    public void test_only_producer_simple_normal_case1() {
        System.setProperty("file_name", "seda_only_producer_simple.properties");
        try {
            for (int i = 0; i < 1000; i++) {
                KafkaBootStraper.get().pub(new TestObj(i));
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @BeforeClass
    public static void before() {
        // test_only_producer_simple_normal_case1
        // System.setProperty("file_name", "seda_only_producer_simple.properties");
        // test_simple_consmer_normal_case2
        // System.setProperty("file_name", "achelous_flow.properties");
        // test_multi_pipeline
        // System.setProperty("file_name", "achelous_multi_pipeline.properties");
        // test_kafka_default_consumer_config
        System.setProperty("file_name", "kafka.properties");
        // test_kafka_default_pub_config
        // System.setProperty("file_name", "kafka_pub.properties");

    }

    @Test
    public void test_kafka_default_consumer_config() {
        try {
            KafkaBootStraper.startSpringConsumer();
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void test_kafka_default_pub_config() {
        try {
            for (int i = 0; i < 1000; i++) {
                KafkaBootStraper.get().pub(new TestObj(i));
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test_simple_consmer_normal_case2() {
        try {
            KafkaBootStraper.startSpringConsumer();
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void test_multi_pipeline() {

        try {
            for (int i = 0; i < 5; i++) {
                KafkaBootStraper.get().pubDefaultKey("producer", new TestObj(i));
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            KafkaBootStraper.get().sub("consumer");
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
