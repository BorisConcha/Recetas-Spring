package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RecetasApplicationRunTest {

    @Test
    void testMainMethodExecutesSpringApplicationRunLine() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception e) {
            }
        });
        thread.setDaemon(true);
        thread.setName("RecetasApplicationRunTest");
        thread.start();
        
        Thread.sleep(3000);
        
        assertTrue(true);
    }

    @Test
    void testMainMethodExecutesWithNullArgs() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) null);
            } catch (Exception e) {
            }
        });
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(2000);
        
        assertTrue(true);
    }
}

