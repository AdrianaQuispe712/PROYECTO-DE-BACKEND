package com.hotel.reservas.controller;

import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.hotel.reservas.login.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cliente createCliente(@Valid @RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @Valid @RequestBody Cliente clienteDetails) {
        return clienteService.findById(id)
                .map(cliente -> {
                    cliente.setNombre(clienteDetails.getNombre());
                    cliente.setEmail(clienteDetails.getEmail());
                    cliente.setTelefono(clienteDetails.getTelefono());
                    Cliente updated = clienteService.save(cliente);
                    return ResponseEntity.ok(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (clienteService.existsById(id)) {
            clienteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mi-perfil")
    public ResponseEntity<Cliente> getMiPerfil() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // No hay usuario autenticado
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        System.out.println("Buscando perfil para usuario: " + username);

        // Buscar el usuario en la base de datos
        Optional<com.hotel.reservas.login.model.Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            System.out.println("No se encontró usuario con username: " + username);
            return ResponseEntity.notFound().build();
        }

        com.hotel.reservas.login.model.Usuario usuario = usuarioOpt.get();
        System.out.println("Usuario encontrado: " + usuario.getId() + " - " + usuario.getUsername());

        // Buscar el cliente asociado al usuario
        Optional<Cliente> clienteOpt = clienteService.findByUsuarioId(usuario.getId());

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            System.out.println("Cliente encontrado: ID=" + cliente.getId() + ", Nombre=" + cliente.getNombre());
            return ResponseEntity.ok(cliente);
        } else {
            System.out.println("NO SE ENCONTRÓ CLIENTE para usuario ID: " + usuario.getId());

            // Depuración: listar todos los clientes y sus usuarios asociados
            List<Cliente> todosClientes = clienteService.findAll();
            System.out.println("Total de clientes en sistema: " + todosClientes.size());
            for (Cliente c : todosClientes) {
                System.out.println("Cliente ID: " + c.getId() + ", Usuario ID: " +
                        (c.getUsuario() != null ? c.getUsuario().getId() : "NULL"));
            }
            return ResponseEntity.notFound().build();
        }
    }
}
