/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.mixtest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dingding.open.achelous.kafka.TestObj;
import com.dingding.open.achelous.kafka.support.AchelousKafka;

/**
 * kafka入口测试
 * 
 * @author surlymo
 * @date Oct 29, 2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:achelous-core.xml")
public class AchelousKafkaSpringTest {

    @Autowired
    AchelousKafka kafka;

    @Test
    public void test_only_producer_simple_normal_case1() {
        System.setProperty("file_name", "seda_only_producer_simple.properties");
        try {
            for (int i = 0; i < 1000; i++) {
                kafka.pub(new TestObj(i));
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @BeforeClass
    public static void before() {
        // test_simple_consmer_normal_case2
        System.setProperty("file_name", "achelous_flow.properties");
    }

    @Test
    public void test_simple_consmer_normal_case2() {
        try {
            kafka.sub();
            Thread.sleep(5000000L);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
