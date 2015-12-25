/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dingding.open.achelous.core.support.BaseException;
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

    private static final Logger logger = Logger.getLogger(KafkaBootStraper.class);

    private static String FILENAME = "achelous.properteis";

    private static volatile boolean isOpen = true;

    static {
        // TODO 这边仅靠properties一种方式.后续需要扩展xml等方式
        Properties prop = new Properties();
        if (StringUtils.isNotBlank(System.getProperty("file_name"))) {
            FILENAME = System.getProperty("file_name");
        }
        InputStream input = ClassLoader.getSystemResourceAsStream(FILENAME);
        try {
            prop.load(input);
        } catch (IOException e) {
            logger.error("[Achelous]error occur while parser core properties file", e);
            throw new BaseException();
        }

        String globalSwitch = prop.getProperty("achelous.switch");
        if (globalSwitch != null && !Boolean.valueOf(globalSwitch).booleanValue()) {
            isOpen = false;
        }

    }

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
