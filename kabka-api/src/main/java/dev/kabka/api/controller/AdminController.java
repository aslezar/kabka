package dev.kabka.api.controller;

import dev.kabka.core.KabkaEngine;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for managing the Kabka messaging engine
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final KabkaEngine engine;
    
    public AdminController(KabkaEngine engine) {
        this.engine = engine;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "service", "Kabka Messaging System",
            "status", "UP",
            "version", "1.0.0"
        );
    }
    
    @PostMapping("/start")
    public Map<String, String> startEngine() {
        engine.start();
        return Map.of("message", "Engine started");
    }
    
    @PostMapping("/stop")
    public Map<String, String> stopEngine() {
        engine.stop();
        return Map.of("message", "Engine stopped");
    }
}
