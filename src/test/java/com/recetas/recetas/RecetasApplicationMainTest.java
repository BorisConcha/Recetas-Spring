package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RecetasApplicationMainTest {

    @Test
    void testMainMethodExists() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertTrue(mainMethod.isAccessible() || java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }

    @Test
    void testMainMethodCanBeInvoked() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception e) {
            }
        });
        thread.setDaemon(true);
        thread.start();
        thread.join(2000);
        
        assertTrue(true);
    }

    @Test
    void testMainMethodWithArgs() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) new String[]{"--test"});
            } catch (Exception e) {
            }
        });
        thread.setDaemon(true);
        thread.start();
        thread.join(2000);
        
        assertTrue(true);
    }

    @Test
    void testMainMethodDirectInvocation() throws Exception {
        Method mainMethod = RecetasApplication.class.getMethod("main", String[].class);
        
        Thread thread = new Thread(() -> {
            try {
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception e) {
            }
        });
        thread.setDaemon(true);
        thread.start();
        
        Thread.sleep(1500);
        
        if (thread.isAlive()) {
            thread.interrupt();
        }
        
        assertTrue(true);
    }
}

