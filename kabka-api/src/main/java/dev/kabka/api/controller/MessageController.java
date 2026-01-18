package dev.kabka.api.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API for producing and consuming messages
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @GetMapping("/consume")
    public Map<String, Object> consume(
            @RequestParam String topic,
            @RequestParam(defaultValue = "0") int partition,
            @RequestParam(defaultValue = "0") long offset) {
        
        // TODO: Use Broker from kabka-core to consume messages
        
        return Map.of(
            "topic", "topic",
            "partition", "partition"
        );
    }
}
