package com.recetas.recetas.dto;

import com.recetas.recetas.util.PasswordValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class RegistroRequest {
    
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre completo debe tener entre 3 y 200 caracteres")
    private String nombreCompleto;
    
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 50, message = "La contraseña debe tener entre 8 y 50 caracteres")
    private String password;
    
    /**
     * Valida la contraseña usando PasswordValidator
     * @return ValidationResult con los errores si los hay
     */
    public PasswordValidator.ValidationResult validatePassword() {
        return PasswordValidator.validate(this.password);
    }
    
    public RegistroRequest() {
    }
    
    public RegistroRequest(String nombreCompleto, String username, String email, String password) {
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}

