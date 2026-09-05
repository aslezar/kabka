package dev.kabka.api.metrics;

import dev.kabka.core.KabkaEngine;
import dev.kabka.core.consumergroup.Consumer;
import dev.kabka.core.consumergroup.ConsumerGroup;
import dev.kabka.core.partition.Partition;
import dev.kabka.core.topic.Topic;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class KabkaMetricsRegistrar {

	public KabkaMetricsRegistrar(KabkaEngine engine, MeterRegistry registry) {
		for (Topic topic : engine.getTopics()) {
			for (Partition partition : topic.getPartitions()) {
				Gauge.builder("kabka.partition.queue.depth", partition, Partition::size).tag("topic", topic.getName())
						.tag("partition", String.valueOf(partition.getPartitionNo())).register(registry);
			}
		}

		for (ConsumerGroup group : engine.getConsumerGroups()) {
			for (Consumer consumer : group.getConsumers()) {
				for (Partition partition : consumer.getPartitions()) {
					Gauge.builder("kabka.consumer.lag", partition,
							p -> p.size() - p.getCommittedOffset(group.getName()))
							.tag("topic", partition.getTopicName())
							.tag("partition", String.valueOf(partition.getPartitionNo())).tag("group", group.getName())
							.register(registry);
				}
			}
		}
	}
}
