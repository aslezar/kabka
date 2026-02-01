package dev.kabka.api.config;

import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.config.TopicConfig;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KabkaProperties {

	private List<TopicConfig> topic;
	private List<ConsumerGroupConfig> consumerGroups;

	public List<TopicConfig> getTopic() {
		return topic;
	}

	public List<ConsumerGroupConfig> getConsumerGroups() {
		return consumerGroups;
	}

	public void setTopic(List<TopicConfig> topic) {
		this.topic = topic;
	}

	public void setConsumerGroups(List<ConsumerGroupConfig> consumerGroups) {
		this.consumerGroups = consumerGroups;
	}
}
