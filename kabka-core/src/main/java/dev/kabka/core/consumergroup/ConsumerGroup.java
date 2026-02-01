package dev.kabka.core.consumergroup;

import java.util.ArrayList;
import java.util.List;

import dev.kabka.core.config.ConsumerConfig;
import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.topic.Topic;

public class ConsumerGroup {
    private final String name;
    private final List<Consumer> consumers;
    // private final Partition[] partitions;
    
    public ConsumerGroup(ConsumerGroupConfig consumerGroupConfig, List<Topic> topics) {
        this.name = consumerGroupConfig.getName();
        this.consumers = new ArrayList<>();
        for (ConsumerConfig consumerConfig: consumerGroupConfig.getConsumers()) {
            this.consumers.add(new Consumer(consumerConfig, topics));
        }
    }
    
    public String getName() {
        return name;
    }
}
