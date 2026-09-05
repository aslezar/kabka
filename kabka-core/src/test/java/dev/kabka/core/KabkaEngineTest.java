package dev.kabka.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.kabka.core.config.ConsumerConfig;
import dev.kabka.core.config.ConsumerConfig.ConsumerTopicConfig;
import dev.kabka.core.config.ConsumerGroupConfig;
import dev.kabka.core.config.TopicConfig;
import dev.kabka.core.exception.GroupNotAssignedException;
import dev.kabka.core.exception.TopicNotFoundException;
import dev.kabka.core.message.Message;
import dev.kabka.core.topic.PushResult;
import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class KabkaEngineTest {

	private TopicConfig topicConfig(String name, int partitions) {
		TopicConfig config = new TopicConfig();
		config.setName(name);
		config.setPartitions(partitions);
		return config;
	}

	private ConsumerGroupConfig groupConfig(String groupName, String consumerName, String topicName,
			List<Integer> partitions) {
		ConsumerTopicConfig topicConfig = new ConsumerTopicConfig();
		topicConfig.setName(topicName);
		topicConfig.setPartitions(partitions);

		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setName(consumerName);
		consumerConfig.setTopics(List.of(topicConfig));

		ConsumerGroupConfig groupConfig = new ConsumerGroupConfig();
		groupConfig.setName(groupName);
		groupConfig.setConsumers(List.of(consumerConfig));
		return groupConfig;
	}

	private KabkaEngine newEngine() {
		List<TopicConfig> topics = List.of(topicConfig("t1", 2));
		List<ConsumerGroupConfig> groups = List.of(groupConfig("g1", "c1", "t1", List.of(0)));
		return new KabkaEngine(groups, topics);
	}

	@Test
	void pushThenPullRoundTrips() {
		KabkaEngine engine = newEngine();
		PushResult result = engine.pushToTopic("t1", "hello".getBytes(), OptionalInt.of(0));
		assertEquals(0, result.partitionNo());
		assertEquals(0, result.offset());

		Message[] messages = engine.pullFromTopic("t1", 0, 0, 10);
		assertEquals(1, messages.length);
		assertEquals("hello", messages[0].getPayloadString());
	}

	@Test
	void unknownTopicThrowsOnPushAndPull() {
		KabkaEngine engine = newEngine();
		assertThrows(TopicNotFoundException.class,
				() -> engine.pushToTopic("nope", "x".getBytes(), OptionalInt.empty()));
		assertThrows(TopicNotFoundException.class, () -> engine.pullFromTopic("nope", 0, 0, 10));
	}

	@Test
	void isPartitionAssignedToGroupReflectsConfiguredAssignment() {
		KabkaEngine engine = newEngine();
		assertTrue(engine.isPartitionAssignedToGroup("g1", "t1", 0));
		assertFalse(engine.isPartitionAssignedToGroup("g1", "t1", 1));
		assertFalse(engine.isPartitionAssignedToGroup("no-such-group", "t1", 0));
	}

	@Test
	void pollAndCommitRoundTripThroughAssignedGroup() {
		KabkaEngine engine = newEngine();
		engine.pushToTopic("t1", "a".getBytes(), OptionalInt.of(0));
		engine.pushToTopic("t1", "b".getBytes(), OptionalInt.of(0));

		assertEquals(0, engine.getCommittedOffset("t1", 0, "g1"));
		Message[] first = engine.pollFromGroup("t1", 0, "g1", 10);
		assertEquals(2, first.length);

		engine.commitOffset("t1", 0, "g1", 1);
		assertEquals(1, engine.getCommittedOffset("t1", 0, "g1"));
		Message[] second = engine.pollFromGroup("t1", 0, "g1", 10);
		assertEquals(1, second.length);
	}

	@Test
	void pollFromUnassignedGroupThrows() {
		KabkaEngine engine = newEngine();
		assertThrows(GroupNotAssignedException.class, () -> engine.pollFromGroup("t1", 1, "g1", 10));
		assertThrows(GroupNotAssignedException.class, () -> engine.commitOffset("t1", 0, "no-such-group", 0));
	}
}
