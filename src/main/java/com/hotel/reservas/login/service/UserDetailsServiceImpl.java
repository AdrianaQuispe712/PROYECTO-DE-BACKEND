package com.hotel.reservas.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.reservas.login.model.Usuario;
import com.hotel.reservas.login.repository.UsuarioRepository;
import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.service.ClienteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ClienteService clienteService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // Convertir roles a authorities
        List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre().name()))
                .collect(Collectors.toList());

        // Verificar si el usuario tiene un cliente asociado y registrarlo en el log
        Optional<Cliente> cliente = clienteService.findByUsuarioId(usuario.getId());
        if (cliente.isPresent()) {
            System.out.println("Usuario " + username + " (ID: " + usuario.getId() + ") tiene cliente asociado con ID: " + cliente.get().getId());
        } else {
            System.out.println("Usuario " + username + " (ID: " + usuario.getId() + ") NO tiene cliente asociado");
        }

        // Imprimir información de depuración
        System.out.println("Usuario autenticado: " + usuario.getId() + " - " + usuario.getUsername());
        System.out.println("Roles: " + authorities.stream().map(a -> a.getAuthority()).collect(Collectors.joining(", ")));

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true, true, true,
                authorities);
    }
}

// import com.hotel.reservas.login.model.Usuario;
// import com.hotel.reservas.login.repository.UsuarioRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;


// import java.util.List;
// import java.util.stream.Collectors;


// @Service
// public class UserDetailsServiceImpl implements UserDetailsService {
//     // Inyectar el repositorio de Usuario para acceder a los datos de usuario
//     // y sus roles 
//     @Autowired
//     private UsuarioRepository usuarioRepository;

//     // Método que carga el usuario por su nombre de usuario (username)
//     // Este método es llamado por el framework de Spring Security durante el proceso de autenticación
//     @Override
//     @Transactional
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         Usuario usuario = usuarioRepository.findByUsername(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

//         // Convertir los roles del usuario a una lista de GrantedAuthority
//         // Esto es necesario para que Spring Security pueda manejar los roles del usuario
//         List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
//                 .map(rol -> {
//                     String enumName = rol.getNombre().name();
//                     // Convierte ROL_ADMIN -> ROLE_ADMIN, ROL_DOCENTE -> ROLE_DOCENTE, etc.
//                     String springRole = enumName.replace("ROL_", "ROLE_");
//                     return new SimpleGrantedAuthority(springRole);
//                 })
//                 .collect(Collectors.toList());

//         // Crear un objeto User de Spring Security con los detalles del usuario
//         // y sus roles (authorities)
//         return new User(usuario.getUsername(), usuario.getPassword(), usuario.isActivo(),
//                 true, true, true, authorities);
//     }
// }
