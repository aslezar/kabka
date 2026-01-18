package dev.kabka.api.config;

import dev.kabka.core.KabkaEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Kabka Core components
 */
@Configuration
public class KabkaConfig {
    
    @Bean
    public KabkaEngine kabkaEngine() {
        KabkaEngine engine = new KabkaEngine();
        engine.start(); // Auto-start on application startup
        return engine;
    }
}
