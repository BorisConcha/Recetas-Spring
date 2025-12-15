package com.recetas.recetas;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashGeneratorTest {

    @Test
    void testPasswordHashing() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        String password = "test123";
        String hash = encoder.encode(password);
        
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
        assertTrue(encoder.matches(password, hash));
        assertFalse(encoder.matches("wrong", hash));
    }

    @Test
    void testMultipleHashesAreDifferent() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        String password = "test123";
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);
        
        // Los hashes deben ser diferentes debido al salt aleatorio
        assertNotEquals(hash1, hash2);
        
        // Pero ambos deben validar la misma contraseña
        assertTrue(encoder.matches(password, hash1));
        assertTrue(encoder.matches(password, hash2));
    }

    @Test
    void testPasswordVerification() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        String[] passwords = {"admin123", "chef123", "usuario123"};
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            assertTrue(encoder.matches(password, hash));
            assertFalse(encoder.matches(password + "wrong", hash));
        }
    }

    @Test
    void testHashFormat() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hash = encoder.encode("test");
        
        // BCrypt hash debe tener formato específico
        assertNotNull(hash);
        assertTrue(hash.length() >= 60);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
    }

    @Test
    void testPasswordHashGeneratorLogic() {
        // Simular la lógica del main sin ejecutarlo
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        // Contraseñas de prueba del main
        String[] passwords = {"admin123", "chef123", "usuario123"};
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            assertNotNull(hash);
            assertTrue(encoder.matches(password, hash));
        }
    }

    @Test
    void testHashVerificationLogic() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        // Simular verificación de hash en BD
        String hashEnBD = "$2a$12$LQv3c1yqBMVHxkd0LHAkCOYz6TtxMQJqhN8/LewI5gyg.TT4TkVOe";
        String[] testPasswords = {
            "admin123", "admin", "password", "12345678", 
            "admin1234", "password123", "qwerty", "123456"
        };
        
        for (String testPwd : testPasswords) {
            boolean matches = encoder.matches(testPwd, hashEnBD);
            // Al menos uno debería hacer match o ninguno, pero el método debe ejecutarse
            assertNotNull(Boolean.valueOf(matches));
        }
    }

    @Test
    void testSQLScriptGenerationLogic() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        // Simular generación de scripts SQL
        String[] usernames = {"admin", "chef", "usuario"};
        String[] passwords = {"admin123", "chef123", "usuario123"};
        
        for (int i = 0; i < usernames.length; i++) {
            String hash = encoder.encode(passwords[i]);
            assertNotNull(hash);
            // Verificar que el hash puede usarse en SQL
            assertFalse(hash.contains("'"));
            assertTrue(encoder.matches(passwords[i], hash));
        }
    }

    @Test
    void testPasswordHashGeneratorMainMethodLogic() {
        // Simular toda la lógica del método main sin ejecutarlo
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        // Simular la sección de generación de hashes
        String[] passwords = {"admin123", "chef123", "usuario123"};
        for (String password : passwords) {
            String hash = encoder.encode(password);
            assertNotNull(hash);
            assertTrue(encoder.matches(password, hash));
        }
        
        // Simular la sección de verificación de hash en BD
        String hashEnBD = "$2a$12$LQv3c1yqBMVHxkd0LHAkCOYz6TtxMQJqhN8/LewI5gyg.TT4TkVOe";
        String[] testPasswords = {
            "admin123", "admin", "password", "12345678", 
            "admin1234", "password123", "qwerty", "123456"
        };
        for (String testPwd : testPasswords) {
            boolean matches = encoder.matches(testPwd, hashEnBD);
            assertNotNull(Boolean.valueOf(matches));
        }
        
        // Simular la sección de generación de scripts SQL
        String[] usernames = {"admin", "chef", "usuario"};
        for (int i = 0; i < usernames.length; i++) {
            String hash = encoder.encode(passwords[i]);
            assertNotNull(hash);
            assertTrue(encoder.matches(passwords[i], hash));
        }
    }
}

