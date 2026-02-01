package dev.kabka.core.config;

import java.util.List;


public class ConsumerGroupConfig {
    private String name;
    private List<ConsumerConfig> consumers;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<ConsumerConfig> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<ConsumerConfig> consumers) {
        this.consumers = consumers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name = ");
        sb.append(name);
        sb.append(", ");
        return sb.toString();
    }
}