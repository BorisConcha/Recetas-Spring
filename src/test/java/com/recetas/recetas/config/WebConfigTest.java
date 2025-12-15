package com.recetas.recetas.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebConfigTest {

    @Test
    void testWebConfigImplementsWebMvcConfigurer() {
        WebConfig webConfig = new WebConfig();
        assertTrue(webConfig instanceof WebMvcConfigurer);
        assertNotNull(webConfig);
    }

    @Test
    void testWebConfigInstantiation() {
        WebConfig webConfig = new WebConfig();
        assertNotNull(webConfig);
        assertTrue(webConfig instanceof WebMvcConfigurer);
    }
}

