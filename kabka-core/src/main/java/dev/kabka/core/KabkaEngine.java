package dev.kabka.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.config.TopicConfig;
import dev.kabka.core.topic.Topic;

/**
 * Main entry point for the Kabka messaging engine. This class manages the core
 * broker functionality.
 */
public class KabkaEngine {

    private static final Logger logger = LoggerFactory.getLogger(KabkaEngine.class);
    private List<Topic> topics;
    private List<ConsumerGroupConfig> consumerGroups;

    public KabkaEngine(List<ConsumerGroupConfig> consumerGroupConfigs, List<TopicConfig> topicConfigs) {
        
        // Constructor logic if needed
        this.topics = new ArrayList<>();
        this.consumerGroups = new ArrayList<>();

        logger.info(consumerGroupConfigs.toString());

        for (TopicConfig topicConfig : topicConfigs) {
            this.topics.add(new Topic(topicConfig.getName(), topicConfig.getPartitions()));
        }

        // for (ConsumerGroupConfig consumerGroupConfig : consumerGroupConfigs) {
        //     this.consumerGroups.add(new Topic(topicConfig.getName(), topicConfig.getPartitions()));
        // }
    }
}
