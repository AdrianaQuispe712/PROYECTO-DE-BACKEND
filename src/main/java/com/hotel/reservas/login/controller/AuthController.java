package com.hotel.reservas.login.controller;

import com.hotel.reservas.login.dto.AuthDTO.JwtResponse;
import com.hotel.reservas.login.dto.AuthDTO.LoginRequest;
import com.hotel.reservas.login.dto.AuthDTO.MessageResponse;
import com.hotel.reservas.login.dto.AuthDTO.SignupRequest;
import com.hotel.reservas.login.model.Rol;
import com.hotel.reservas.login.model.Rol.NombreRol;
import com.hotel.reservas.login.model.Usuario;
import com.hotel.reservas.login.repository.RolRepository;
import com.hotel.reservas.login.repository.UsuarioRepository;
import com.hotel.reservas.login.security.JwtUtils;
import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        return ResponseEntity.ok(new JwtResponse(jwt,
                usuario.getId(),
                userDetails.getUsername(),
                usuario.getEmail(),
                new HashSet<>(roles)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El nombre de usuario ya está en uso."));
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso."));
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(signUpRequest.getUsername());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setPassword(encoder.encode(signUpRequest.getPassword()));
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setApellido(signUpRequest.getApellido());

        Set<Rol> roles = new HashSet<>();
        Rol clienteRol = rolRepository.findByNombre(NombreRol.ROL_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        roles.add(clienteRol);

        usuario.setRoles(roles);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        System.out.println("Usuario guardado con ID: " + usuarioGuardado.getId());

        Cliente cliente = new Cliente();
        cliente.setNombre(signUpRequest.getNombre() + " " + signUpRequest.getApellido());
        cliente.setEmail(signUpRequest.getEmail());
        cliente.setUsuario(usuarioGuardado);
        Cliente clienteGuardado = clienteService.save(cliente);
        System.out.println("Cliente creado con ID: " + clienteGuardado.getId() + " para usuario ID: " + usuarioGuardado.getId());

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal().equals("anonymousUser"))) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(new JwtResponse(
                    null,
                    usuario.getId(),
                    userDetails.getUsername(),
                    usuario.getEmail(),
                    roles
            ));
        }

        return ResponseEntity.ok(new MessageResponse("No hay sesión activa"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Sesión cerrada exitosamente!"));
    }
}
