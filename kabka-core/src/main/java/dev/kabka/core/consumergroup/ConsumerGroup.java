package dev.kabka.core.consumergroup;

import dev.kabka.core.config.ConsumerConfig;
import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.partition.Partition;
import dev.kabka.core.topic.Topic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsumerGroup {
	private final String name;
	private final List<Consumer> consumers;

	public ConsumerGroup(ConsumerGroupConfig consumerGroupConfig, List<Topic> topics) {
		this.name = consumerGroupConfig.getName();
		this.consumers = new ArrayList<>();
		for (ConsumerConfig consumerConfig : consumerGroupConfig.getConsumers()) {
			this.consumers.add(new Consumer(consumerConfig, topics));
		}
	}

	public String getName() {
		return name;
	}

	public List<Consumer> getConsumers() {
		return Collections.unmodifiableList(consumers);
	}

	public boolean hasPartition(Partition partition) {
		return consumers.stream().anyMatch(c -> c.getPartitions().contains(partition));
	}
}
