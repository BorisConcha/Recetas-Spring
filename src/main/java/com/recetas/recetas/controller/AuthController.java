package com.recetas.recetas.controller;

import com.recetas.recetas.dto.LoginRequest;
import com.recetas.recetas.dto.LoginResponse;
import com.recetas.recetas.dto.RegistroRequest;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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
}

