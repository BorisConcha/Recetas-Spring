package com.recetas.recetas;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        System.out.println("===========================================");
        System.out.println("GENERADOR DE HASHES BCRYPT");
        System.out.println("===========================================\n");
        
        // Contraseñas de prueba
        String[] passwords = {"admin123", "chef123", "usuario123"};
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println("Verificación: " + encoder.matches(password, hash));
            System.out.println();
        }
        
        System.out.println("===========================================");
        System.out.println("VERIFICAR HASH ACTUAL DE LA BD");
        System.out.println("===========================================\n");
        
        String hashEnBD = "$2a$12$LQv3c1yqBMVHxkd0LHAkCOYz6TtxMQJqhN8/LewI5gyg.TT4TkVOe";
        
        System.out.println("Hash en BD: " + hashEnBD);
        System.out.println("\nProbando contraseñas comunes:");
        
        String[] testPasswords = {
            "admin123", "admin", "password", "12345678", 
            "admin1234", "password123", "qwerty", "123456"
        };
        
        for (String testPwd : testPasswords) {
            boolean matches = encoder.matches(testPwd, hashEnBD);
            System.out.println("  '" + testPwd + "': " + (matches ? "✓ MATCH" : "✗ no match"));
        }
        
        System.out.println("\n===========================================");
        System.out.println("SCRIPTS SQL PARA ACTUALIZAR LA BD");
        System.out.println("===========================================\n");
        
        // Generar scripts SQL
        System.out.println("-- Actualizar usuario 'admin' con password 'admin123'");
        System.out.println("UPDATE usuarios SET password = '" + encoder.encode("admin123") + "' WHERE username = 'admin';");
        System.out.println();
        
        System.out.println("-- Actualizar usuario 'chef' con password 'chef123'");
        System.out.println("UPDATE usuarios SET password = '" + encoder.encode("chef123") + "' WHERE username = 'chef';");
        System.out.println();
        
        System.out.println("-- Actualizar usuario 'usuario' con password 'usuario123'");
        System.out.println("UPDATE usuarios SET password = '" + encoder.encode("usuario123") + "' WHERE username = 'usuario';");
        System.out.println();
        
        System.out.println("===========================================");
        System.out.println("NOTAS IMPORTANTES:");
        System.out.println("===========================================");
        System.out.println("1. Cada vez que ejecutes este programa, los hashes serán DIFERENTES");
        System.out.println("   (BCrypt incluye un 'salt' aleatorio)");
        System.out.println("2. Copia el script SQL y ejecútalo en tu base de datos");
        System.out.println("3. Reinicia la aplicación Spring Boot");
        System.out.println("4. Intenta loguearte con las credenciales indicadas");
        System.out.println("===========================================");
    }
}
