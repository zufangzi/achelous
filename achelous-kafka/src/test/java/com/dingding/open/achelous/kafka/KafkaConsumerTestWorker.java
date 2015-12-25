package com.dingding.open.achelous.kafka;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dingding.open.achelous.kafka.support.KafkaContext;
import com.dingding.open.achelous.worker.MessageWorker;

@Component
public class KafkaConsumerTestWorker implements MessageWorker<TestObj, List<KafkaContext>> {

    @Override
    public List<KafkaContext> proc(TestObj message) {
        System.out.println("now in..." + message.getA());

        TestObj obj = new TestObj(99999);

        KafkaContext context = new KafkaContext("rePub", obj);
        return Arrays.asList(context);
    }

}
