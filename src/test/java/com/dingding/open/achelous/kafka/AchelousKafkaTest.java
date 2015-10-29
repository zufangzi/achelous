/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka;

import org.junit.Assert;
import org.junit.Test;

/**
 * kafka入口测试
 * 
 * @author surlymo
 * @date Oct 29, 2015
 */
public class AchelousKafkaTest {

    @Test
    public void test_pub_normal() {
        try {
            AchelousKafka.INSTANCE.pub("msgCenter", "hello", "kitty");
        } catch (Exception e) {
            Assert.fail();
        }

    }
}
