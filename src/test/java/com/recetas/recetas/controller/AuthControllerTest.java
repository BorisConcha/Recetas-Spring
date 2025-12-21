package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.LoginRequest;
import com.recetas.recetas.dto.RecuperarPasswordRequest;
import com.recetas.recetas.dto.RegistroRequest;
import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.RoleRepository;
import com.recetas.recetas.repository.UsuarioRepository;
import com.recetas.recetas.service.DetalleUserService;
import com.recetas.recetas.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private DetalleUserService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;
    private Usuario usuario;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setNombre("ROLE_USER");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");
        usuario.setPassword("encodedPassword");

        userDetails = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void testLogin_Exitoso() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void testLogin_ErrorAutenticacion() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegistro_Exitoso() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setUsername("newuser");
        registroRequest.setEmail("newuser@example.com");
        registroRequest.setPassword("Password123!"); // Contraseña válida que cumple todas las validaciones
        registroRequest.setNombreCompleto("New User");

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByNombre("ROLE_USER")).thenReturn(Optional.of(role));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(userDetailsService.loadUserByUsername("newuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/registro")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(usuarioRepository).findByUsername("newuser");
        verify(usuarioRepository).findByEmail("newuser@example.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testRegistro_UsernameYaExiste() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setUsername("existinguser");
        registroRequest.setEmail("newuser@example.com");
        registroRequest.setPassword("Password123!"); // Contraseña válida
        registroRequest.setNombreCompleto("New User");

        when(usuarioRepository.findByUsername("existinguser")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/auth/registro")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El username ya está en uso"));

        verify(usuarioRepository).findByUsername("existinguser");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testRegistro_EmailYaExiste() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setUsername("newuser");
        registroRequest.setEmail("existing@example.com");
        registroRequest.setPassword("Password123!"); // Contraseña válida
        registroRequest.setNombreCompleto("New User");

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/api/auth/registro")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El email ya está en uso"));

        verify(usuarioRepository).findByUsername("newuser");
        verify(usuarioRepository).findByEmail("existing@example.com");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testRegistro_PasswordInvalida() throws Exception {
        // Usar una contraseña que pase @Size pero falle PasswordValidator
        // Password sin números ni caracteres especiales
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setUsername("newuser");
        registroRequest.setEmail("newuser@example.com");
        registroRequest.setPassword("Password"); // Pasa @Size pero falla PasswordValidator (sin números ni caracteres especiales)
        registroRequest.setNombreCompleto("New User");

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        // No mockeamos roleRepository porque la validación de contraseña debe fallar antes

        mockMvc.perform(post("/api/auth/registro")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La contraseña no cumple con los requisitos de seguridad"))
                .andExpect(jsonPath("$.errores").exists());

        verify(usuarioRepository, never()).findByUsername(anyString());
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(roleRepository, never()).findByNombre(anyString());
        verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    void testRegistro_RolNoEncontrado() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setUsername("newuser");
        registroRequest.setEmail("newuser@example.com");
        registroRequest.setPassword("Password123!"); // Contraseña válida
        registroRequest.setNombreCompleto("New User");

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(roleRepository.findByNombre("ROLE_USER")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/registro")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroRequest)))
                .andExpect(status().isBadRequest());

        verify(usuarioRepository).findByUsername("newuser");
        verify(usuarioRepository).findByEmail("newuser@example.com");
        verify(roleRepository).findByNombre("ROLE_USER");
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testObtenerPerfil_Exitoso() throws Exception {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        
        mockMvc.perform(get("/api/auth/perfil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
        
        verify(usuarioRepository).findByUsername("testuser");
    }
    
    @Test
    @WithMockUser(username = "nonexistent")
    void testObtenerPerfil_UsuarioNoEncontrado() throws Exception {
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/auth/perfil"))
                .andExpect(status().isNotFound());
        
        verify(usuarioRepository).findByUsername("nonexistent");
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testActualizarPerfil_Exitoso() throws Exception {
        com.recetas.recetas.dto.ActualizarPerfilRequest request = new com.recetas.recetas.dto.ActualizarPerfilRequest();
        request.setNombreCompleto("Test User Updated");
        request.setEmail("updated@example.com");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        mockMvc.perform(put("/api/auth/perfil")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Perfil actualizado correctamente"));
        
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository).save(any(Usuario.class));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testActualizarPerfil_EmailYaEnUso() throws Exception {
        com.recetas.recetas.dto.ActualizarPerfilRequest request = new com.recetas.recetas.dto.ActualizarPerfilRequest();
        request.setNombreCompleto("Test User");
        request.setEmail("existing@example.com");
        
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setEmail("existing@example.com");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(otroUsuario));
        
        mockMvc.perform(put("/api/auth/perfil")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El email ya está en uso por otro usuario"));
        
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCambiarPassword_Exitoso() throws Exception {
        com.recetas.recetas.dto.CambiarPasswordRequest request = new com.recetas.recetas.dto.CambiarPasswordRequest();
        request.setPasswordActual("oldPassword");
        request.setNuevaPassword("NewPassword123!");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword123!")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        mockMvc.perform(put("/api/auth/cambiar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada correctamente"));
        
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository).save(any(Usuario.class));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCambiarPassword_ContrasenaActualIncorrecta() throws Exception {
        com.recetas.recetas.dto.CambiarPasswordRequest request = new com.recetas.recetas.dto.CambiarPasswordRequest();
        request.setPasswordActual("wrongPassword");
        request.setNuevaPassword("NewPassword123!");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        
        mockMvc.perform(put("/api/auth/cambiar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La contraseña actual es incorrecta"));
        
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCambiarPassword_NuevaPasswordInvalida() throws Exception {
        com.recetas.recetas.dto.CambiarPasswordRequest request = new com.recetas.recetas.dto.CambiarPasswordRequest();
        request.setPasswordActual("oldPassword");
        request.setNuevaPassword("weak"); // Contraseña inválida
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        
        mockMvc.perform(put("/api/auth/cambiar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La nueva contraseña no cumple con los requisitos de seguridad"))
                .andExpect(jsonPath("$.errores").exists());
        
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCambiarPassword_UsuarioNoEncontrado() throws Exception {
        com.recetas.recetas.dto.CambiarPasswordRequest request = new com.recetas.recetas.dto.CambiarPasswordRequest();
        request.setPasswordActual("oldPassword");
        request.setNuevaPassword("NewPassword123!");
        
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/auth/cambiar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
        
        verify(usuarioRepository).findByUsername("testuser");
        verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testCambiarPassword_Excepcion() throws Exception {
        com.recetas.recetas.dto.CambiarPasswordRequest request = new com.recetas.recetas.dto.CambiarPasswordRequest();
        request.setPasswordActual("oldPassword");
        request.setNuevaPassword("NewPassword123!");
        
        when(usuarioRepository.findByUsername("testuser")).thenThrow(new RuntimeException("Error de base de datos"));
        
        mockMvc.perform(put("/api/auth/cambiar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error al cambiar la contraseña")));
        
        verify(usuarioRepository).findByUsername("testuser");
    }
    
    @Test
    void testRecuperarPassword_Exitoso() throws Exception {
        RecuperarPasswordRequest request = new RecuperarPasswordRequest();
        request.setEmail("test@example.com");
        
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        
        mockMvc.perform(post("/api/auth/recuperar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Si el email existe, se enviará un enlace de recuperación"));
        
        verify(usuarioRepository).findByEmail("test@example.com");
    }
    
    @Test
    void testRecuperarPassword_UsuarioNoEncontrado() throws Exception {
        // Por seguridad, siempre retorna el mismo mensaje aunque el usuario no exista
        RecuperarPasswordRequest request = new RecuperarPasswordRequest();
        request.setEmail("noexiste@example.com");
        
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/auth/recuperar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Si el email existe, se enviará un enlace de recuperación"));
        
        verify(usuarioRepository).findByEmail("noexiste@example.com");
    }
    
    @Test
    void testRecuperarPassword_Excepcion() throws Exception {
        RecuperarPasswordRequest request = new RecuperarPasswordRequest();
        request.setEmail("test@example.com");
        
        when(usuarioRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Error de base de datos"));
        
        mockMvc.perform(post("/api/auth/recuperar-password")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error al procesar la solicitud")));
        
        verify(usuarioRepository).findByEmail("test@example.com");
    }
}

