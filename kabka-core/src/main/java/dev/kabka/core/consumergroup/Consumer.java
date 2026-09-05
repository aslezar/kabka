package dev.kabka.core.consumergroup;

import dev.kabka.core.config.ConsumerConfig;
import dev.kabka.core.config.ConsumerConfig.ConsumerTopicConfig;
import dev.kabka.core.partition.Partition;
import dev.kabka.core.topic.Topic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Consumer {
	private final ConsumerConfig consumerConfig;
	private final String name;
	private final List<Partition> partitions;

	public Consumer(ConsumerConfig consumerConfig, List<Topic> allTopics) {
		this.consumerConfig = consumerConfig;
		this.name = consumerConfig.getName();
		this.partitions = new ArrayList<>();
		for (Topic topic : allTopics) {
			for (ConsumerTopicConfig topicConfig : consumerConfig.getTopics()) {
				if (topic.getName().equals(topicConfig.getName())) {
					for (Integer partitionNumber : topicConfig.getPartitions()) {
						if (!topic.isValidPartitionNumber(partitionNumber)) {
							throw new IllegalArgumentException(
									"given partition number doesnt exist in topic : " + topic.getName());
						}
						this.partitions.add(topic.getPartitions()[partitionNumber]);
					}
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public List<Partition> getPartitions() {
		return Collections.unmodifiableList(partitions);
	}
}
