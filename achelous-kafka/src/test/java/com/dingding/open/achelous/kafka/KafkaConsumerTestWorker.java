package com.dingding.open.achelous.kafka;

import com.dingding.open.achelous.kafka.support.KafkaContext;
import com.dingding.open.achelous.worker.MessageWorker;

public class KafkaConsumerTestWorker implements MessageWorker<TestObj, KafkaContext> {

    @Override
    public KafkaContext proc(TestObj message) {
        System.out.println("now in..." + message.getA());
        KafkaContext context = new KafkaContext();
        context.setKey("rePub");
        context.setValue("hello");
        return context;
    }

}
