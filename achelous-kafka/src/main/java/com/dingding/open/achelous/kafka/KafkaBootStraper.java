/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.kafka.support.AchelousKafka;

/**
 * kafka启动器。
 * 
 * @author surlymo
 * @date Nov 3, 2015
 */
@Component
public class KafkaBootStraper implements ApplicationContextAware {

    @SuppressWarnings("unused")
    @Autowired
    private AchelousKafka orderMock;

    private static AchelousKafka kafka;

    private static volatile boolean isOpen = true;

    public static AchelousKafka get() {
        return kafka;
    }

    public static void startSpringConsumer() {
        if (isOpen) {
            kafka.sub();
        }
    }

    public static void startConsumer() {
        if (isOpen) {
            AchelousKafka.INSTANCE.sub();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        kafka = context.getBean(AchelousKafka.class);
    }
}
