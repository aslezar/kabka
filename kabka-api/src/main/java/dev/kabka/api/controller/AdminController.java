package dev.kabka.api.controller;

import dev.kabka.core.KabkaEngine;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		return Map.of("service", "Kabka Messaging System", "status", "UP", "version", "1.0.2");
	}
}
