package com.recetas.recetas.controller;

import com.recetas.recetas.dto.*;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import com.recetas.recetas.util.PasswordValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private DetalleUserService userDetailsService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            
            String token = jwtService.generateToken(userDetails);
            
            LoginResponse response = new LoginResponse(token, loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autenticación: " + e.getMessage());
        }
    }
    
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest registroRequest) {
        try {
            // Validar contraseña con PasswordValidator
            PasswordValidator.ValidationResult passwordValidation = registroRequest.validatePassword();
            if (!passwordValidation.isValid()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "La contraseña no cumple con los requisitos de seguridad");
                response.put("errores", passwordValidation.getErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (usuarioRepository.findByUsername(registroRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El username ya está en uso");
            }
            
            if (usuarioRepository.findByEmail(registroRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El email ya está en uso");
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombreCompleto(registroRequest.getNombreCompleto());
            usuario.setUsername(registroRequest.getUsername());
            usuario.setEmail(registroRequest.getEmail());
            usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
            usuario.setEnabledBoolean(true);
            
            Role roleUser = roleRepository.findByNombre("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(roleUser);
            usuario.setRoles(roles);
            
            usuarioRepository.save(usuario);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
            String token = jwtService.generateToken(userDetails);
            
            LoginResponse response = new LoginResponse(token, usuario.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }
    
    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequest request) {
        try {
            var usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
            if (usuarioOpt.isEmpty()) {
                // Por seguridad, no revelamos si el email existe o no
                return ResponseEntity.ok(Map.of("mensaje", "Si el email existe, se enviará un enlace de recuperación"));
            }
            
            // En una implementación real, aquí se enviaría un email con un token de recuperación
            // Por ahora, solo retornamos un mensaje genérico
            return ResponseEntity.ok(Map.of("mensaje", "Si el email existe, se enviará un enlace de recuperación"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", usuario.getUsername());
            response.put("nombreCompleto", usuario.getNombreCompleto());
            response.put("email", usuario.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al obtener el perfil: " + e.getMessage());
        }
    }
    
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(@Valid @RequestBody ActualizarPerfilRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar si el email ya está en uso por otro usuario
            var emailUsuarioOpt = usuarioRepository.findByEmail(request.getEmail());
            if (emailUsuarioOpt.isPresent() && !emailUsuarioOpt.get().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El email ya está en uso por otro usuario");
            }
            
            usuario.setNombreCompleto(request.getNombreCompleto());
            usuario.setEmail(request.getEmail());
            usuarioRepository.save(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Perfil actualizado correctamente");
            response.put("usuario", Map.of(
                "username", usuario.getUsername(),
                "nombreCompleto", usuario.getNombreCompleto(),
                "email", usuario.getEmail()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }
    
    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar contraseña actual
            if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La contraseña actual es incorrecta");
            }
            
            // Validar nueva contraseña
            PasswordValidator.ValidationResult passwordValidation = request.validateNuevaPassword();
            if (!passwordValidation.isValid()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "La nueva contraseña no cumple con los requisitos de seguridad");
                response.put("errores", passwordValidation.getErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
            usuarioRepository.save(usuario);
            
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al cambiar la contraseña: " + e.getMessage());
        }
    }
}

