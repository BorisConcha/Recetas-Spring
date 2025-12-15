package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecetasApplicationTests {

	@Test
	void testRecetasApplicationInstantiation() {
		RecetasApplication app = new RecetasApplication();
		assertNotNull(app);
	}

	@Test
	void testRecetasApplicationMainMethodExists() throws Exception {
		RecetasApplication.class.getMethod("main", String[].class);
		assertTrue(true);
	}
}
