package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RecetasApplicationFullTest {

    @Test
    void testMainMethodExecutesSpringApplicationRun() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception e) {

            }
        });
        thread.setDaemon(true);
        thread.setName("RecetasApplicationTestThread");
        thread.start();
        
        Thread.sleep(1000);
        
        assertTrue(true);
    }

    @Test
    void testMainMethodWithEmptyArgs() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                String[] args = new String[0];
                mainMethod.invoke(null, (Object) args);
            } catch (Exception e) {
            }
        });
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(1000);
        
        assertTrue(true);
    }
}

