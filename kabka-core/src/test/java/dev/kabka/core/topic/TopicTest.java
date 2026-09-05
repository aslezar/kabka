package dev.kabka.core.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.kabka.core.exception.InvalidPartitionException;
import dev.kabka.core.message.Message;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class TopicTest {

	private Message message() {
		return new Message("x".getBytes());
	}

	@Test
	void roundRobinCyclesThroughPartitionsInOrder() {
		Topic topic = new Topic("t", 3);

		assertEquals(0, topic.push(message(), OptionalInt.empty()).partitionNo());
		assertEquals(1, topic.push(message(), OptionalInt.empty()).partitionNo());
		assertEquals(2, topic.push(message(), OptionalInt.empty()).partitionNo());
		assertEquals(0, topic.push(message(), OptionalInt.empty()).partitionNo());
	}

	@Test
	void explicitPartitionIsRespected() {
		Topic topic = new Topic("t", 3);
		PushResult result = topic.push(message(), OptionalInt.of(2));
		assertEquals(2, result.partitionNo());
		assertEquals(0, result.offset());
	}

	@Test
	void invalidExplicitPartitionThrowsOnPushAndPull() {
		Topic topic = new Topic("t", 3);
		assertThrows(InvalidPartitionException.class, () -> topic.push(message(), OptionalInt.of(5)));
		assertThrows(InvalidPartitionException.class, () -> topic.pull(5, 0, 10));
	}
}
