/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.mixtest;

import org.junit.Assert;
import org.junit.Test;

import com.dingding.open.achelous.kafka.KafkaBootStraper;
import com.dingding.open.achelous.kafka.TestObj;
import com.dingding.open.achelous.kafka.support.AchelousKafka;

/**
 * kafka入口测试
 * 
 * @author surlymo
 * @date Oct 29, 2015
 */
public class AchelousKafkaTest {

    @Test
    public void test_only_producer_simple_normal_case1() {
        System.setProperty("file_name", "seda_only_producer_simple.properties");
        try {
            for (int i = 0; i < 1000; i++) {
                AchelousKafka.INSTANCE.pub(new TestObj(i));
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test_only_consumer_simple_normal_case1() {
        try {
            AchelousKafka.INSTANCE.sub();
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test_multi_producer_consmer_producer_normal_case1() {
        try {
            for (int i = 0; i < 1000; i++) {
                AchelousKafka.INSTANCE.pubDefaultKey("msgCenter", new TestObj(i));
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test_multi_producer_consmer_consumer_normal_case1() {
        try {
            AchelousKafka.INSTANCE.sub("msgCenter2");
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test_simple_consmer_normal_case2() {
        System.setProperty("file_name", "achelous_flow.properties");
        try {
            KafkaBootStraper.startConsumer();
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
