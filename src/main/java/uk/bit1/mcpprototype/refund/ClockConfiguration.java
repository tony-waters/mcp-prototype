package uk.bit1.mcpprototype.refund;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Configuration
class ClockConfiguration {

    @Bean
    Clock clock(@Value("${refund.clock.instant:}") String fixedInstant) {
        if (fixedInstant == null || fixedInstant.isBlank()) {
            return Clock.systemUTC();
        }
        return Clock.fixed(Instant.parse(fixedInstant), ZoneOffset.UTC);
    }
}
