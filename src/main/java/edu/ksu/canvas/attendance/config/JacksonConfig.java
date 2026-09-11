package edu.ksu.canvas.attendance.config;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Jackson ObjectMapper with defensive settings against CVEs:
 * - CVE-2020-36518: Deeply nested JSON objects causing stack overflow (DoS)
 * - CVE-2022-42003: JSON objects exploiting UNWRAP_SINGLE_VALUE_ARRAYS with unbounded resource consumption
 * - CVE-2022-42004: BeanDeserializer._deserialize with unbounded resource consumption
 * 
 * These security measures are applied even though Jackson 2.17.1 is already patched.
 * The defensive configuration provides additional protection against potential bypasses.
 * 
 * Note: Spring Boot 1.2.8 doesn't support Jackson2ObjectMapperBuilderCustomizer (added in 1.4+),
 * so this uses direct ObjectMapper bean configuration instead.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Disable features that can be exploited
        mapper.disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Apply streaming constraints to limit nesting depth and string/number lengths
        // This provides defense-in-depth for:
        // - CVE-2020-36518: Limits nesting depth to prevent stack overflow
        // - CVE-2022-42003 & CVE-2022-42004: Limits resource consumption
        mapper.getFactory().setStreamReadConstraints(
            StreamReadConstraints.builder()
                .maxNestingDepth(200)              // Prevent stack overflow from deep nesting
                .maxNumberLength(100)              // Prevent resource exhaustion from large numbers
                .maxStringLength(5_000_000)        // Limit individual strings to 5MB
                .build()
        );

        return mapper;
    }
}

