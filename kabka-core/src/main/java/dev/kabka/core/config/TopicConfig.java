package dev.kabka.core.config;

import java.util.Map;

public class TopicConfig {
    private String name;
    private int partitions;
    private Map<String, String> config;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPartitions() { return partitions; }
    public void setPartitions(int partitions) { this.partitions = partitions; }

    public Map<String, String> getConfig() { return config; }
    public void setConfig(Map<String, String> config) { this.config = config; }
}
