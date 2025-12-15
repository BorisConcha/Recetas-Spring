package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashGeneratorMainTest {

    @Test
    void testMainMethodExists() throws Exception {
        Method mainMethod = PasswordHashGenerator.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
    }

    @Test
    void testMainMethodExecution() throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);
        
        try {
            System.setOut(newOut);
            
            Method mainMethod = PasswordHashGenerator.class.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
            
            String output = baos.toString();
            assertNotNull(output);
            assertTrue(output.length() > 0);
            assertTrue(output.contains("GENERADOR") || output.contains("BCrypt") || output.contains("Hash"));
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testMainMethodExecutionComplete() throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);
        
        try {
            System.setOut(newOut);
            
            Method mainMethod = PasswordHashGenerator.class.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
            
            String output = baos.toString();
            assertNotNull(output);
            assertTrue(output.length() > 100);
            assertTrue(output.contains("GENERADOR") || output.contains("VERIFICAR") || output.contains("SCRIPTS"));
            
        } finally {
            System.setOut(originalOut);
        }
    }
}

