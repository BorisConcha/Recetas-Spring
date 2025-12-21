package com.recetas.recetas.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {
    
    @Test
    void testValidatePasswordNull() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("La contraseña es obligatoria"));
    }
    
    @Test
    void testValidatePasswordEmpty() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("La contraseña es obligatoria"));
    }
    
    @Test
    void testValidatePasswordTooShort() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Abc1!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("al menos 8 caracteres")));
    }
    
    @Test
    void testValidatePasswordTooLong() {
        String longPassword = "A".repeat(51) + "b1!";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(longPassword);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("más de 50 caracteres")));
    }
    
    @Test
    void testValidatePasswordNoUppercase() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("password123!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("letra mayúscula")));
    }
    
    @Test
    void testValidatePasswordNoLowercase() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("PASSWORD123!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("letra minúscula")));
    }
    
    @Test
    void testValidatePasswordNoNumber() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Password!");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("número")));
    }
    
    @Test
    void testValidatePasswordNoSpecialCharacter() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Password123");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("carácter especial")));
    }
    
    @Test
    void testValidatePasswordValid() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Password123!");
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    void testValidatePasswordMultipleErrors() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("abc");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 1);
    }
    
    @Test
    void testValidatePasswordWithSpecialCharacters() {
        String[] specialChars = {"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "[", "]", "{", "}", ";", ":", "'", "\"", "\\", "|", ",", ".", "<", ">", "/", "?"};
        
        for (String specialChar : specialChars) {
            String password = "Password123" + specialChar;
            PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
            assertTrue(result.isValid(), "Password with " + specialChar + " should be valid");
        }
    }
    
    @Test
    void testValidationResultDefault() {
        PasswordValidator.ValidationResult result = new PasswordValidator.ValidationResult();
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    void testValidationResultAddError() {
        PasswordValidator.ValidationResult result = new PasswordValidator.ValidationResult();
        result.addError("Error de prueba");
        
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().contains("Error de prueba"));
    }
    
    @Test
    void testValidationResultGetErrorsWhenEmpty() {
        PasswordValidator.ValidationResult result = new PasswordValidator.ValidationResult();
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.isValid());
    }
    
    @Test
    void testValidationResultMultipleErrors() {
        PasswordValidator.ValidationResult result = new PasswordValidator.ValidationResult();
        result.addError("Error 1");
        result.addError("Error 2");
        result.addError("Error 3");
        
        assertFalse(result.isValid());
        assertEquals(3, result.getErrors().size());
        assertTrue(result.getErrors().contains("Error 1"));
        assertTrue(result.getErrors().contains("Error 2"));
        assertTrue(result.getErrors().contains("Error 3"));
    }
    
    @Test
    void testValidationResultGetErrorsReturnsList() {
        PasswordValidator.ValidationResult result = new PasswordValidator.ValidationResult();
        result.addError("Test error");
        
        var errors = result.getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        // Verificar que getErrors retorna una lista mutable
        errors.add("Another error");
        assertEquals(2, result.getErrors().size());
    }
    
    @Test
    void testPasswordValidatorConstructor() {
        // Instanciar la clase para cubrir el constructor por defecto
        PasswordValidator validator = new PasswordValidator();
        assertNotNull(validator);
    }
}

