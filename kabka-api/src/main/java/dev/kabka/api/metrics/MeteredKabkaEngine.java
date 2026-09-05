package dev.kabka.api.metrics;

import dev.kabka.core.KabkaEngine;
import dev.kabka.core.message.Message;
import dev.kabka.core.topic.PushResult;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.OptionalInt;

public class MeteredKabkaEngine {

	private final KabkaEngine engine;
	private final MeterRegistry registry;

	public MeteredKabkaEngine(KabkaEngine engine, MeterRegistry registry) {
		this.engine = engine;
		this.registry = registry;
	}

	public PushResult pushToTopic(String topicName, byte[] message, OptionalInt partitionNo) {
		Timer.Sample sample = Timer.start(registry);
		String outcome = "success";
		try {
			PushResult result = engine.pushToTopic(topicName, message, partitionNo);
			registry.counter("kabka.messages.produced", "topic", topicName).increment();
			return result;
		} catch (RuntimeException e) {
			outcome = "error";
			throw e;
		} finally {
			sample.stop(Timer.builder("kabka.produce.latency").tag("topic", topicName).tag("outcome", outcome)
					.register(registry));
		}
	}

	public Message[] pullFromTopic(String topicName, int partitionNo, long seqNo, int batchSize) {
		Timer.Sample sample = Timer.start(registry);
		String outcome = "success";
		try {
			Message[] result = engine.pullFromTopic(topicName, partitionNo, seqNo, batchSize);
			registry.counter("kabka.messages.consumed", "topic", topicName).increment(result.length);
			return result;
		} catch (RuntimeException e) {
			outcome = "error";
			throw e;
		} finally {
			sample.stop(Timer.builder("kabka.consume.latency").tag("topic", topicName)
					.tag("partition", String.valueOf(partitionNo)).tag("outcome", outcome).register(registry));
		}
	}

	public Message[] pollFromGroup(String topicName, int partitionNo, String groupName, int batchSize) {
		Timer.Sample sample = Timer.start(registry);
		String outcome = "success";
		try {
			Message[] result = engine.pollFromGroup(topicName, partitionNo, groupName, batchSize);
			registry.counter("kabka.messages.consumed", "topic", topicName).increment(result.length);
			return result;
		} catch (RuntimeException e) {
			outcome = "error";
			throw e;
		} finally {
			sample.stop(Timer.builder("kabka.poll.latency").tag("topic", topicName)
					.tag("partition", String.valueOf(partitionNo)).tag("group", groupName).tag("outcome", outcome)
					.register(registry));
		}
	}

	public void commitOffset(String topicName, int partitionNo, String groupName, long offset) {
		Timer.Sample sample = Timer.start(registry);
		String outcome = "success";
		try {
			engine.commitOffset(topicName, partitionNo, groupName, offset);
		} catch (RuntimeException e) {
			outcome = "error";
			throw e;
		} finally {
			sample.stop(Timer.builder("kabka.commit.latency").tag("topic", topicName)
					.tag("partition", String.valueOf(partitionNo)).tag("group", groupName).tag("outcome", outcome)
					.register(registry));
		}
	}

	public long getCommittedOffset(String topicName, int partitionNo, String groupName) {
		return engine.getCommittedOffset(topicName, partitionNo, groupName);
	}
}
