package com.recetas.recetas.dto;

import com.recetas.recetas.util.PasswordValidator;
import jakarta.validation.constraints.NotBlank;

public class CambiarPasswordRequest {
    
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String passwordActual;
    
    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaPassword;
    
    public CambiarPasswordRequest() {
    }
    
    public String getPasswordActual() {
        return passwordActual;
    }
    
    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }
    
    public String getNuevaPassword() {
        return nuevaPassword;
    }
    
    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
    

    public PasswordValidator.ValidationResult validateNuevaPassword() {
        return PasswordValidator.validate(this.nuevaPassword);
    }
}

