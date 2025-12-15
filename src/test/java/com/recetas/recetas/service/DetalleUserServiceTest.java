package com.recetas.recetas.service;

import com.recetas.recetas.model.Role;
import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetalleUserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DetalleUserService detalleUserService;

    private Usuario usuario;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setNombre("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("$2a$10$testPasswordHash");
        usuario.setEmail("test@example.com");
        usuario.setEnabled(1);
        usuario.setRoles(roles);
    }

    @Test
    void testLoadUserByUsername_Exitoso() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("$2a$10$testPasswordHash", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertFalse(userDetails.getAuthorities().isEmpty());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UsuarioNoEncontrado() {
        when(usuarioRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            detalleUserService.loadUserByUsername("nonexistent");
        });

        verify(usuarioRepository).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_SinRoles() {
        usuario.setRoles(new HashSet<>());
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertFalse(userDetails.getAuthorities().isEmpty());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_RolSinPrefijo() {
        Role roleSinPrefijo = new Role();
        roleSinPrefijo.setId(2L);
        roleSinPrefijo.setNombre("USER");

        Set<Role> roles = new HashSet<>();
        roles.add(roleSinPrefijo);
        usuario.setRoles(roles);

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UsuarioDeshabilitado() {
        usuario.setEnabled(0);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_SinPassword() {
        usuario.setPassword(null);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertThrows(UsernameNotFoundException.class, () -> {
            detalleUserService.loadUserByUsername("testuser");
        });

        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_PasswordVacia() {
        usuario.setPassword("");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        assertThrows(UsernameNotFoundException.class, () -> {
            detalleUserService.loadUserByUsername("testuser");
        });

        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_TrimUsername() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("  testuser  ");

        assertNotNull(userDetails);
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_PasswordNoHasheada() {
        usuario.setPassword("plaintext");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("plaintext", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_MultipleRoles() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setNombre("ROLE_USER");
        
        Role role2 = new Role();
        role2.setId(2L);
        role2.setNombre("ROLE_ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        usuario.setRoles(roles);

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals(2, userDetails.getAuthorities().size());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_EnabledNull() {
        usuario.setEnabled(null);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_PasswordStartsWith2b() {
        usuario.setPassword("$2b$10$testPasswordHash");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("$2b$10$testPasswordHash", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_WithRolesNotEmpty() {
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertFalse(userDetails.getAuthorities().isEmpty());
        verify(usuarioRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_PasswordStartsWith2a() {
        usuario.setPassword("$2a$10$testPasswordHash");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("$2a$10$testPasswordHash", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_PasswordNoStartsWith2aOr2b() {
        usuario.setPassword("plaintext");
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = detalleUserService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("plaintext", userDetails.getPassword());
    }
}

