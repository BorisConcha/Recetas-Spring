package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashGeneratorMainTest {

    @Test
    void testMainMethodExists() throws Exception {
        // Verificar que el método main existe
        Method mainMethod = PasswordHashGenerator.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
    }

    @Test
    void testMainMethodExecution() throws Exception {
        // Capturar System.out para evitar que imprima durante el test
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);
        
        try {
            System.setOut(newOut);
            
            // Invocar el método main
            Method mainMethod = PasswordHashGenerator.class.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
            
            // Verificar que se ejecutó (hay salida)
            String output = baos.toString();
            assertNotNull(output);
            assertTrue(output.length() > 0);
            // Verificar que contiene contenido esperado
            assertTrue(output.contains("GENERADOR") || output.contains("BCrypt") || output.contains("Hash"));
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testMainMethodExecutionComplete() throws Exception {
        // Ejecutar el método main completo para cubrir todas las líneas
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);
        
        try {
            System.setOut(newOut);
            
            // Invocar el método main
            Method mainMethod = PasswordHashGenerator.class.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
            
            // Verificar que se ejecutó completamente
            String output = baos.toString();
            assertNotNull(output);
            assertTrue(output.length() > 100); // Debe tener bastante salida
            // Verificar que contiene las secciones principales
            assertTrue(output.contains("GENERADOR") || output.contains("VERIFICAR") || output.contains("SCRIPTS"));
            
        } finally {
            System.setOut(originalOut);
        }
    }
}

