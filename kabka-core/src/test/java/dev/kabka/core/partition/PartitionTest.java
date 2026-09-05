package dev.kabka.core.partition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.kabka.core.exception.InvalidOffsetException;
import dev.kabka.core.message.Message;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

class PartitionTest {

	private Message message(String payload) {
		return new Message(payload.getBytes());
	}

	@Test
	void pushAssignsSequentialOffsets() {
		Partition partition = new Partition("t", 0);
		assertEquals(0, partition.push(message("a")));
		assertEquals(1, partition.push(message("b")));
		assertEquals(2, partition.push(message("c")));
	}

	@Test
	void pullReturnsRequestedRangeAndRejectsOutOfRange() {
		Partition partition = new Partition("t", 0);
		partition.push(message("a"));
		partition.push(message("b"));

		Message[] result = partition.pull(0, 10);
		assertEquals(2, result.length);
		assertEquals("a", result[0].getPayloadString());

		assertThrows(InvalidOffsetException.class, () -> partition.pull(5, 10));
		assertThrows(InvalidOffsetException.class, () -> partition.pull(-1, 10));
	}

	@Test
	void pollStartsAtZeroForUnseenGroup() {
		Partition partition = new Partition("t", 0);
		partition.push(message("a"));

		Message[] result = partition.poll("group-1", 10);
		assertEquals(1, result.length);
	}

	@Test
	void pollWithoutCommitReturnsSameBatchTwice() {
		Partition partition = new Partition("t", 0);
		partition.push(message("a"));
		partition.push(message("b"));

		Message[] first = partition.poll("group-1", 10);
		Message[] second = partition.poll("group-1", 10);

		assertEquals(first.length, second.length);
		assertEquals(first[0].getOffset(), second[0].getOffset());
	}

	@Test
	void commitThenPollAdvances() {
		Partition partition = new Partition("t", 0);
		partition.push(message("a"));
		partition.push(message("b"));
		partition.push(message("c"));

		partition.commitOffset("group-1", 1);
		Message[] result = partition.poll("group-1", 10);

		assertEquals(2, result.length);
		assertEquals(1, result[0].getOffset());
	}

	@Test
	void commitRejectsOutOfRangeOffsets() {
		Partition partition = new Partition("t", 0);
		partition.push(message("a"));

		assertThrows(InvalidOffsetException.class, () -> partition.commitOffset("group-1", -1));
		assertThrows(InvalidOffsetException.class, () -> partition.commitOffset("group-1", 2));
	}

	@Test
	void concurrentPushesProduceNoDuplicateOrMissingOffsets() throws InterruptedException {
		Partition partition = new Partition("t", 0);
		int threads = 20;
		int pushesPerThread = 50;
		int totalPushes = threads * pushesPerThread;

		ExecutorService pool = Executors.newFixedThreadPool(threads);
		List<Long> offsets = new CopyOnWriteArrayList<>();

		for (int i = 0; i < threads; i++) {
			pool.submit(() -> {
				for (int j = 0; j < pushesPerThread; j++) {
					offsets.add(partition.push(message("x")));
				}
			});
		}
		pool.shutdown();
		assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

		assertEquals(totalPushes, partition.size());
		Set<Long> unique = new HashSet<>(offsets);
		assertEquals(totalPushes, unique.size());
		Set<Long> expected = LongStream.range(0, totalPushes).boxed().collect(Collectors.toSet());
		assertEquals(expected, unique);
	}
}
