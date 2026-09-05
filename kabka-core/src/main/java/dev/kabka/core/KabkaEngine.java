package dev.kabka.core;

import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.config.TopicConfig;
import dev.kabka.core.consumergroup.ConsumerGroup;
import dev.kabka.core.exception.GroupNotAssignedException;
import dev.kabka.core.exception.TopicNotFoundException;
import dev.kabka.core.message.Message;
import dev.kabka.core.partition.Partition;
import dev.kabka.core.topic.PushResult;
import dev.kabka.core.topic.Topic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Kabka messaging engine. This class manages the core
 * broker functionality.
 */
public class KabkaEngine {

	private static final Logger logger = LoggerFactory.getLogger(KabkaEngine.class);
	private final List<Topic> topics;
	private final List<ConsumerGroup> consumerGroups;

	public KabkaEngine(List<ConsumerGroupConfig> consumerGroupConfigs, List<TopicConfig> topicConfigs) {
		this.topics = new ArrayList<>();
		this.consumerGroups = new ArrayList<>();

		for (TopicConfig topicConfig : topicConfigs) {
			this.topics.add(new Topic(topicConfig.getName(), topicConfig.getPartitions()));
		}

		for (ConsumerGroupConfig consumerGroupConfig : consumerGroupConfigs) {
			this.consumerGroups.add(new ConsumerGroup(consumerGroupConfig, topics));
		}
	}

	public PushResult pushToTopic(String topicName, byte[] message, OptionalInt partitionNo) {
		Message msg = new Message(message);
		PushResult result = findTopicOrThrow(topicName).push(msg, partitionNo);
		logger.info("Message pushed to topic: " + topicName);
		return result;
	}

	public Message[] pullFromTopic(String topicName, int partitionNo, long seqNo, int batchSize) {
		return findTopicOrThrow(topicName).pull(partitionNo, seqNo, batchSize);
	}

	public boolean isPartitionAssignedToGroup(String groupName, String topicName, int partitionNo) {
		Partition partition = findTopicOrThrow(topicName).getPartition(partitionNo);
		ConsumerGroup group = findGroupByName(groupName);
		return group != null && group.hasPartition(partition);
	}

	public Message[] pollFromGroup(String topicName, int partitionNo, String groupName, int batchSize) {
		return resolveAssignedPartition(topicName, partitionNo, groupName).poll(groupName, batchSize);
	}

	public void commitOffset(String topicName, int partitionNo, String groupName, long offset) {
		resolveAssignedPartition(topicName, partitionNo, groupName).commitOffset(groupName, offset);
	}

	public long getCommittedOffset(String topicName, int partitionNo, String groupName) {
		return resolveAssignedPartition(topicName, partitionNo, groupName).getCommittedOffset(groupName);
	}

	public List<Topic> getTopics() {
		return Collections.unmodifiableList(topics);
	}

	public List<ConsumerGroup> getConsumerGroups() {
		return Collections.unmodifiableList(consumerGroups);
	}

	private Partition resolveAssignedPartition(String topicName, int partitionNo, String groupName) {
		Partition partition = findTopicOrThrow(topicName).getPartition(partitionNo);
		ConsumerGroup group = findGroupByName(groupName);
		if (group == null || !group.hasPartition(partition)) {
			throw new GroupNotAssignedException(
					"Group " + groupName + " is not assigned to " + topicName + "/" + partitionNo);
		}
		return partition;
	}

	private Topic findTopicOrThrow(String topicName) {
		for (Topic topic : topics) {
			if (topic.getName().equals(topicName)) {
				return topic;
			}
		}
		throw new TopicNotFoundException("Topic not found: " + topicName);
	}

	private ConsumerGroup findGroupByName(String groupName) {
		for (ConsumerGroup group : consumerGroups) {
			if (group.getName().equals(groupName)) {
				return group;
			}
		}
		return null;
	}
}
