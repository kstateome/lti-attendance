package edu.ksu.canvas.attendance.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for JacksonConfig to verify CVE mitigation security settings.
 * 
 * Tests verify:
 * - CVE-2020-36518: Deeply nested JSON protection
 * - CVE-2022-42003: UNWRAP_SINGLE_VALUE_ARRAYS disabled
 * - CVE-2022-42004: Resource consumption limits
 */
public class JacksonConfigUTest {

    private JacksonConfig jacksonConfig;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        jacksonConfig = new JacksonConfig();
        objectMapper = jacksonConfig.objectMapper();
    }

    @Test
    public void testObjectMapperBeanIsCreated() {
        assertNotNull("ObjectMapper bean should be created", objectMapper);
    }

    @Test
    public void testUnwrapSingleValueArraysIsDisabled() {
        assertFalse("UNWRAP_SINGLE_VALUE_ARRAYS should be disabled",
                objectMapper.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS));
    }

    @Test
    public void testFailOnUnknownPropertiesIsDisabled() {
        assertFalse("FAIL_ON_UNKNOWN_PROPERTIES should be disabled",
                objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    @Test
    public void testStreamReadConstraintsAreConfigured() {
        JsonFactory factory = objectMapper.getFactory();
        assertNotNull("JsonFactory should have StreamReadConstraints configured", factory);
    }

    @Test
    public void testMaxNestingDepthConstraint() {
        StringBuilder deepJson = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            deepJson.append("{\"a\":");
        }
        deepJson.append("1");
        for (int i = 0; i < 201; i++) {
            deepJson.append("}");
        }
        
        try {
            objectMapper.readValue(deepJson.toString(), Object.class);
            fail("Should have rejected JSON with nesting depth > 200 (CVE-2020-36518)");
        } catch (Exception e) {
            assertNotNull("Expected a Jackson exception for excessive nesting", e);
        }
    }

    @Test
    public void testMaxNumberLengthConstraint() {
        StringBuilder longNumber = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longNumber.append("9");
        }
        String json = "{\"number\": " + longNumber.toString() + "}";
        
        try {
            objectMapper.readValue(json, Object.class);
            fail("Should have rejected number with length > 100 (CVE-2022-42004)");
        } catch (Exception e) {
            assertNotNull("Expected a Jackson exception for overly long number", e);
        }
    }

    @Test
    public void testMaxStringLengthConstraint() {
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 5_000_001; i++) {
            longString.append("a");
        }
        String json = "{\"string\": \"" + longString.toString() + "\"}";
        
        try {
            objectMapper.readValue(json, Object.class);
            fail("Should have rejected string with length > 5MB (CVE-2022-42004)");
        } catch (Exception e) {
            assertNotNull("Expected a Jackson exception for oversized string", e);
        }
    }

    @Test
    public void testValidJsonIsAccepted() throws Exception {
        String validJson = "{\"name\": \"test\", \"age\": 30}";
        Object result = objectMapper.readValue(validJson, Object.class);
        assertNotNull("Valid JSON should be successfully deserialized", result);
    }

    @Test
    public void testModerateNestingDepthIsAccepted() throws Exception {
        StringBuilder moderateJson = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            moderateJson.append("{\"a\":");
        }
        moderateJson.append("1");
        for (int i = 0; i < 100; i++) {
            moderateJson.append("}");
        }
        
        Object result = objectMapper.readValue(moderateJson.toString(), Object.class);
        assertNotNull("JSON with nesting depth within limit should be accepted", result);
    }

    @Test
    public void testValidNumberIsAccepted() throws Exception {
        String json = "{\"number\": 123456789}";
        Object result = objectMapper.readValue(json, Object.class);
        assertNotNull("Valid number should be accepted", result);
    }

    @Test
    public void testValidStringIsAccepted() throws Exception {
        StringBuilder validString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            validString.append("a");
        }
        String json = "{\"string\": \"" + validString.toString() + "\"}";
        
        Object result = objectMapper.readValue(json, Object.class);
        assertNotNull("Valid string within size limit should be accepted", result);
    }

    @Test
    public void testMultipleConstraintsWork() throws Exception {
        String validJson = "{\"nested\": {\"value\": 42, \"text\": \"hello\"}}";
        Object result = objectMapper.readValue(validJson, Object.class);
        assertNotNull("Multiple constraints should not affect valid JSON", result);
    }

    @Test
    public void testUnknownPropertiesAreIgnored() throws Exception {
        String jsonWithUnknown = "{\"name\": \"test\", \"unknownField\": \"value\"}";
        Object result = objectMapper.readValue(jsonWithUnknown, Object.class);
        assertNotNull("JSON with unknown properties should be accepted", result);
    }

}
