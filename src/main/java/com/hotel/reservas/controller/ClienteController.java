package com.hotel.reservas.controller;

import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.hotel.reservas.login.model.Usuario;
import com.hotel.reservas.login.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lista de clientes obtenida correctamente");
            response.put("data", clientes);
            response.put("total", clientes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse("Error al obtener la lista de clientes: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getClienteById(@PathVariable Long id) {
        try {
            Optional<Cliente> cliente = clienteService.findById(id);
            if (cliente.isPresent()) {
                return successResponse("Cliente encontrado", cliente.get());
            } else {
                return notFoundResponse("Cliente no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            return errorResponse("Error al buscar cliente: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCliente(@Valid @RequestBody Cliente cliente) {
        try {
            if (clienteService.existsByEmail(cliente.getEmail())) {
                return conflictResponse("Ya existe un cliente con este email");
            }

            Cliente nuevoCliente = clienteService.save(cliente);
            return createdResponse("Cliente creado exitosamente", nuevoCliente);
        } catch (Exception e) {
            return errorResponse("Error al crear cliente: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCliente(@PathVariable Long id, @Valid @RequestBody Cliente clienteDetails) {
        try {
            Optional<Cliente> clienteOpt = clienteService.findById(id);
            if (clienteOpt.isEmpty()) {
                return notFoundResponse("Cliente no encontrado con ID: " + id);
            }

            Cliente cliente = clienteOpt.get();

            if (!cliente.getEmail().equals(clienteDetails.getEmail()) &&
                clienteService.existsByEmail(clienteDetails.getEmail())) {
                return conflictResponse("Ya existe otro cliente con este email");
            }

            cliente.setNombre(clienteDetails.getNombre());
            cliente.setEmail(clienteDetails.getEmail());
            cliente.setTelefono(clienteDetails.getTelefono());

            Cliente clienteActualizado = clienteService.save(cliente);
            return successResponse("Cliente actualizado exitosamente", clienteActualizado);
        } catch (Exception e) {
            return errorResponse("Error al actualizar cliente: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCliente(@PathVariable Long id) {
        try {
            if (!clienteService.existsById(id)) {
                return notFoundResponse("Cliente no encontrado con ID: " + id);
            }

            if (clienteService.tieneReservasActivas(id)) {
                return conflictResponse("No se puede eliminar el cliente porque tiene reservas activas");
            }

            clienteService.deleteById(id);
            return successResponse("Cliente eliminado exitosamente", null);
        } catch (Exception e) {
            return errorResponse("Error al eliminar cliente: " + e.getMessage());
        }
    }

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> getMiPerfil() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

            if (usuarioOpt.isEmpty()) {
                return notFoundResponse("Usuario no encontrado");
            }

            Cliente cliente = usuarioOpt.get().getCliente();
            if (cliente == null) {
                return notFoundResponse("No se encontró perfil de cliente asociado");
            }

            return successResponse("Perfil obtenido correctamente", cliente);
        } catch (Exception e) {
            return errorResponse("Error al obtener perfil: " + e.getMessage());
        }
    }

    @PutMapping("/mi-perfil/actualizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> actualizarMiPerfil(@Valid @RequestBody Cliente clienteDetails) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

            if (usuarioOpt.isEmpty()) {
                return notFoundResponse("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();
            Cliente cliente = usuario.getCliente();

            if (cliente == null) {
                return notFoundResponse("Perfil de cliente no encontrado");
            }

            if (!cliente.getEmail().equals(clienteDetails.getEmail()) &&
                clienteService.existsByEmail(clienteDetails.getEmail())) {
                return conflictResponse("Ya existe otro cliente con este email");
            }

            cliente.setNombre(clienteDetails.getNombre());
            cliente.setTelefono(clienteDetails.getTelefono());
            cliente.setEmail(clienteDetails.getEmail());

            Cliente clienteActualizado = clienteService.save(cliente);
            return successResponse("Perfil actualizado exitosamente", clienteActualizado);
        } catch (Exception e) {
            return errorResponse("Error al actualizar perfil: " + e.getMessage());
        }
    }

    // MÉTODOS DE RESPUESTA UNIFICADOS

    private ResponseEntity<Map<String, Object>> successResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createdResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ResponseEntity<Map<String, Object>> notFoundResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    private ResponseEntity<Map<String, Object>> conflictResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
