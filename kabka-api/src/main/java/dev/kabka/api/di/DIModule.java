package dev.kabka.api.di;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.kabka.api.config.KabkaProperties;
import dev.kabka.core.KabkaEngine;

@Configuration
public class DIModule {
    private final KabkaProperties kabkaProperties;

    public DIModule(KabkaProperties kabkaProperties1) {
        this.kabkaProperties = kabkaProperties1;
    }

    @Bean
    public KabkaEngine kabkaEngine() {
        KabkaEngine engine = new KabkaEngine(kabkaProperties.getConsumerGroups(), kabkaProperties.getTopic());
        // TODO: configure `engine` using `kafkaProperties` (topics, consumer groups)
        return engine;
    }
}