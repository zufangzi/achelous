package com.dingding.open.achelous.kafka.benchmark;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dingding.open.achelous.kafka.support.KafkaContext;
import com.dingding.open.achelous.worker.MessageWorker;

@Component
public class KafkaConsumerTestWorker2 implements MessageWorker<AgentKpDetailsData, List<KafkaContext>> {

    @Override
    public List<KafkaContext> proc(AgentKpDetailsData message) {
        System.out.println("now in..." + message.getReason());

        message.setReason("我有很多原因的呀...");

        // KafkaContext context = new KafkaContext("rePub", message);
        return null;
    }

}
