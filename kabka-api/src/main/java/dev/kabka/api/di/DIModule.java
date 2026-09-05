package dev.kabka.api.di;

import dev.kabka.api.config.KabkaProperties;
import dev.kabka.api.metrics.MeteredKabkaEngine;
import dev.kabka.core.KabkaEngine;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DIModule {
	private final KabkaProperties kabkaProperties;

	public DIModule(KabkaProperties kabkaProperties1) {
		this.kabkaProperties = kabkaProperties1;
	}

	@Bean
	public KabkaEngine kabkaEngine() {
		return new KabkaEngine(kabkaProperties.getConsumerGroups(), kabkaProperties.getTopic());
	}

	@Bean
	public MeteredKabkaEngine meteredKabkaEngine(KabkaEngine kabkaEngine, MeterRegistry registry) {
		return new MeteredKabkaEngine(kabkaEngine, registry);
	}
}
