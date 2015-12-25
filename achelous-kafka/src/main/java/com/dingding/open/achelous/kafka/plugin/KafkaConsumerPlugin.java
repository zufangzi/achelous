/**
 * DingDing.com Inc.
 * Copyright (c) 2000-2016 All Rights Reserved.
 */
package com.dingding.open.achelous.kafka.plugin;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.dingding.open.achelous.core.InvokerCore;
import com.dingding.open.achelous.core.plugin.AbstractPlugin;
import com.dingding.open.achelous.core.plugin.PluginName;
import com.dingding.open.achelous.core.support.ConfigConstant;
import com.dingding.open.achelous.core.support.Context;
import com.dingding.open.achelous.core.support.PropertiesUtils;
import com.dingding.open.achelous.kafka.support.KafkaPluginTypes;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * kafka消费者的插件
 * 
 * @author surlymo
 * @date Oct 27, 2015
 */
@PluginName(KafkaPluginTypes.KAFKA_CONSUMER)
public class KafkaConsumerPlugin extends AbstractPlugin {

	private static final Logger logger = Logger.getLogger(KafkaConsumerPlugin.class);

	private static final ConfigConstant CONF_ZKCONFIG = new ConfigConstant("zkconfig", "127.0.0.1:2181");
	private static final ConfigConstant CONF_GROUP = new ConfigConstant("group", "default_group");

	@Override
	public Object doWork(InvokerCore core, final Context context, final Map<String, String> config) throws Throwable {
		if (!notExhaust()) {// 理论上consumer阶段只能被调用一次在一个pipeline里面
			return null;
		}
		Properties props = new Properties();
		PropertiesUtils.put(props, "zookeeper.connect", CONF_ZKCONFIG, config);
		PropertiesUtils.put(props, "group.id", CONF_GROUP, config);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

		logger.info("[ACHELOUS]kafka consumer plugin finish initialization");
		return consumer;
	}

}
