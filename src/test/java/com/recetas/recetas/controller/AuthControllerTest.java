package com.recetas.recetas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.recetas.dto.LoginRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        registroRequest.setPassword("password");
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
        registroRequest.setPassword("password");
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
        registroRequest.setPassword("password");
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
    void testRegistro_RolNoEncontrado() throws Exception {
        RegistroRequest registroRequest = new RegistroRequest();
        registroRequest.setUsername("newuser");
        registroRequest.setEmail("newuser@example.com");
        registroRequest.setPassword("password");
        registroRequest.setNombreCompleto("New User");

        when(usuarioRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
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
}

