package dev.kabka.api.controller;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.kabka.core.KabkaEngine;
import dev.kabka.core.message.Message;

/**
 * REST API for producing and consuming messages
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KabkaEngine engine;

    public MessageController(KabkaEngine engine) {
        this.engine = engine;
    }

    @GetMapping("/pull")
    public Map<String, Object> consume(
            @RequestParam String topic,
            @RequestParam(defaultValue = "0") int partition,
            @RequestParam(defaultValue = "10") int batchSize,
            @RequestParam(defaultValue = "0") long offset) {

        Message[] messages = engine.pullFromTopic(topic, partition, offset, batchSize);

        List<String> payloads = java.util.Arrays.stream(messages)
            .map(m -> m.getPayloadString())
            .toList();

        return Map.of(
            "topic", topic,
            "partition", partition,
            "messages", payloads
        );
    }

    @PostMapping("/push")
    public Map<String, Object> produce(
            @RequestParam String topic,
            @RequestParam() Integer partition,
            @RequestParam String message) {

        engine.pushToTopic(topic, message.getBytes(), OptionalInt.of(partition));

        return Map.of(
                "status", "queued"
        );
    }
}
