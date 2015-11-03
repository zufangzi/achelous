/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka;

import com.dingding.open.achelous.kafka.support.AchelousKafka;

/**
 * kafka启动器
 * 
 * @author surlymo
 * @date Nov 3, 2015
 */
public class KafkaBootStraper {
    public static void startConsumer() {
        AchelousKafka.INSTANCE.sub();
    }

    public static void startProducer() {
        AchelousKafka.INSTANCE.init();
    }
}
