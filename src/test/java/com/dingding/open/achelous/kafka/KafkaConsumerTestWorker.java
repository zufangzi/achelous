package com.dingding.open.achelous.kafka;

import com.dingding.open.achelous.core.worker.MessageWorker;

public class KafkaConsumerTestWorker implements MessageWorker<TestObj> {

    @Override
    public void proc(TestObj message) {
        System.out.println("now in..." + message.getA());
    }

}
