package dev.kabka.core.config;

import java.util.List;


public class ConsumerConfig{
    
    private String name;
    private List<ConsumerTopicConfig> topics;

    public String getName() {
        return name;
    }

    public List<ConsumerTopicConfig> getTopics() {
        return topics;
    }

    public static class ConsumerTopicConfig {
        private String name;
        private List<Integer> partitions;

        public String getName() {
            return name;
        }

        public List<Integer> getPartitions() {
            return partitions;
        }
    }

}
