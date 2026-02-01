package dev.kabka.core;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.config.TopicConfig;
import dev.kabka.core.consumergroup.ConsumerGroup;
import dev.kabka.core.message.Message;
import dev.kabka.core.topic.Topic;

/**
 * Main entry point for the Kabka messaging engine. This class manages the core
 * broker functionality.
 */
public class KabkaEngine {

    private static final Logger logger = LoggerFactory.getLogger(KabkaEngine.class);
    private final List<Topic> topics;
    private final List<ConsumerGroup> consumerGroups;

    public KabkaEngine(List<ConsumerGroupConfig> consumerGroupConfigs, List<TopicConfig> topicConfigs) {

        // Constructor logic if needed
        this.topics = new ArrayList<>();
        this.consumerGroups = new ArrayList<>();

        logger.info(consumerGroupConfigs.toString());

        for (TopicConfig topicConfig : topicConfigs) {
            this.topics.add(new Topic(topicConfig.getName(), topicConfig.getPartitions()));
        }

        for (ConsumerGroupConfig consumerGroupConfig : consumerGroupConfigs) {
            this.consumerGroups.add(new ConsumerGroup(consumerGroupConfig, topics));
        }
    }

    public void pushToTopic(String topicName, byte[] message, OptionalInt partitionNo) throws RuntimeException{
        // Implementation for pushing message to topic
        Message msg = new Message(message);

        for (Topic topic : topics) {
            if (topic.getName().equals(topicName)) {
                boolean success = topic.push(msg, partitionNo);
                if (success) {
                    logger.info("Message pushed to topic: " + topicName);
                } else {
                    logger.error("Failed to push message to topic: " + topicName);
                    throw new RuntimeException("Failed to push message to topic: " + topicName);
                }
                return;
            }
        }

        logger.error("Topic not found: " + topicName);
        throw new RuntimeException("Topic not found: " + topicName);
    }

    public Message[] pullFromTopic(String topicName, int partitionNo, long seqNo, int batchSize) throws RuntimeException {
        // Implementation for pulling messages from topic
        for (Topic topic : topics) {
            if (topic.getName().equals(topicName)) {
                return topic.pull(partitionNo, seqNo, batchSize);
            }
        }

        logger.error("Topic not found: " + topicName);
        throw new RuntimeException("Topic not found: " + topicName);
    }
}
