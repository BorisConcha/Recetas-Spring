package com.recetas.recetas.service;

import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class DetalleUserService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username.trim());
        
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            System.out.println("ADVERTENCIA: Usuario sin roles asignados");
            System.out.println("   El usuario debe tener al menos un rol en USUARIO_ROLES");
        } else {
            System.out.println("   - Cantidad de roles: " + usuario.getRoles().size());
            usuario.getRoles().forEach(role -> 
                System.out.println("     * Rol: " + role.getNombre())
            );
        }
        
        Collection<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(role -> {
                    String roleName = role.getNombre();
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());
        
        if (authorities.isEmpty()) {
            System.out.println("Asignando rol por defecto: ROLE_USER");
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        boolean isEnabled = usuario.getEnabled() != null && usuario.getEnabled() == 1;
        
        System.out.println("   - Enabled (boolean): " + isEnabled);
        System.out.println("   - Authorities cargadas: " + authorities);
        
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            System.out.println("ERROR: Usuario sin contraseña");
            throw new UsernameNotFoundException("Usuario sin contraseña configurada");
        }
        
        if (!usuario.getPassword().startsWith("$2a$") && !usuario.getPassword().startsWith("$2b$")) {
            System.out.println("ADVERTENCIA: La contraseña NO parece estar hasheada con BCrypt");
            System.out.println("   Las contraseñas deben empezar con $2a$ o $2b$");
        }
        
        
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                isEnabled,
                true,
                true,
                true,
                authorities
        );
    }
}
