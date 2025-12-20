package com.recetas.recetas.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator {
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;
    
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        
        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }
        
        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
    

    public static ValidationResult validate(String password) {
        ValidationResult result = new ValidationResult();
        
        if (password == null || password.isEmpty()) {
            result.addError("La contraseña es obligatoria");
            return result;
        }
        
        if (password.length() < MIN_LENGTH) {
            result.addError("La contraseña debe tener al menos " + MIN_LENGTH + " caracteres");
        }
        
        if (password.length() > MAX_LENGTH) {
            result.addError("La contraseña no puede tener más de " + MAX_LENGTH + " caracteres");
        }
        
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            result.addError("La contraseña debe contener al menos una letra mayúscula");
        }
        
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            result.addError("La contraseña debe contener al menos una letra minúscula");
        }
        
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            result.addError("La contraseña debe contener al menos un número");
        }
        
        if (!Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) {
            result.addError("La contraseña debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}|;':\",./<>?)");
        }
        
        return result;
    }
}

