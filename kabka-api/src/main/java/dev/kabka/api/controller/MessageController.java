package dev.kabka.api.controller;

import dev.kabka.api.metrics.MeteredKabkaEngine;
import dev.kabka.core.message.Message;
import dev.kabka.core.topic.PushResult;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for producing and consuming messages
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

	private final MeteredKabkaEngine engine;

	public MessageController(MeteredKabkaEngine engine) {
		this.engine = engine;
	}

	private record MessageView(long offset, Instant timestamp, String payload) {
	}

	@GetMapping("/pull")
	public Map<String, Object> consume(@RequestParam String topic, @RequestParam(defaultValue = "0") int partition,
			@RequestParam(defaultValue = "10") int batchSize, @RequestParam(defaultValue = "0") long offset) {

		Message[] messages = engine.pullFromTopic(topic, partition, offset, batchSize);
		List<MessageView> payloads = java.util.Arrays.stream(messages)
				.map(m -> new MessageView(m.getOffset(), m.getTimestamp(), m.getPayloadString())).toList();

		return Map.of("topic", topic, "partition", partition, "messages", payloads);
	}

	@PostMapping("/push")
	public Map<String, Object> produce(@RequestParam String topic, @RequestParam(required = false) Integer partition,
			@RequestParam String message) {

		OptionalInt partitionNo = partition == null ? OptionalInt.empty() : OptionalInt.of(partition);
		PushResult result = engine.pushToTopic(topic, message.getBytes(), partitionNo);

		return Map.of("status", "queued", "topic", topic, "partition", result.partitionNo(), "offset", result.offset());
	}

	@GetMapping("/poll")
	public Map<String, Object> poll(@RequestParam String topic, @RequestParam int partition, @RequestParam String group,
			@RequestParam(defaultValue = "10") int batchSize) {

		long committedOffset = engine.getCommittedOffset(topic, partition, group);
		Message[] messages = engine.pollFromGroup(topic, partition, group, batchSize);
		List<MessageView> payloads = java.util.Arrays.stream(messages)
				.map(m -> new MessageView(m.getOffset(), m.getTimestamp(), m.getPayloadString())).toList();

		return Map.of("topic", topic, "partition", partition, "group", group, "committedOffset", committedOffset,
				"nextOffset", committedOffset + messages.length, "messages", payloads);
	}

	@PostMapping("/commit")
	public Map<String, Object> commit(@RequestParam String topic, @RequestParam int partition,
			@RequestParam String group, @RequestParam long offset) {

		engine.commitOffset(topic, partition, group, offset);

		return Map.of("status", "committed", "topic", topic, "partition", partition, "group", group, "offset", offset);
	}
}
