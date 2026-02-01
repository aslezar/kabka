package dev.kabka.core.config;

import java.util.List;

public class ConsumerConfig {

	private String name;
	private List<ConsumerTopicConfig> topics;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<ConsumerTopicConfig> getTopics() {
		return topics;
	}

	public void setTopics(List<ConsumerTopicConfig> topics) {
		this.topics = topics;
	}

	public static class ConsumerTopicConfig {
		private String name;
		private List<Integer> partitions;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setPartitions(List<Integer> partitions) {
			this.partitions = partitions;
		}

		public List<Integer> getPartitions() {
			return partitions;
		}
	}

}
