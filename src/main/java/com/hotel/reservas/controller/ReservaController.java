package com.hotel.reservas.controller;

import com.hotel.reservas.dto.ReservaDTO;
import com.hotel.reservas.model.Reserva;
import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.service.ReservaService;
import com.hotel.reservas.service.ClienteService;
import com.hotel.reservas.login.model.Usuario;
import com.hotel.reservas.login.repository.UsuarioRepository;
import com.hotel.reservas.validation.BusinessException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // SOLO ADMIN VE TODAS LAS RESERVAS
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllReservas() {
        try {
            List<Reserva> reservas = reservaService.findAll();
            List<ReservaDTO> reservasDTO = reservas.stream().map(reserva -> new ReservaDTO(
                reserva.getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getCliente().getId(),
                reserva.getHabitacion().getId()
            )).toList();
            
            return ResponseEntity.ok(reservasDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // CLIENTE VE SOLO SUS RESERVAS
    @GetMapping("/mis-reservas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> getMisReservas() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Buscar usuario y cliente
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            
            Optional<Cliente> clienteOpt = clienteService.findByUsuarioId(usuarioOpt.get().getId());
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Cliente no encontrado");
            }
            
            List<Reserva> misReservas = reservaService.findByClienteId(clienteOpt.get().getId());
            List<ReservaDTO> reservasDTO = misReservas.stream().map(reserva -> new ReservaDTO(
                reserva.getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getCliente().getId(),
                reserva.getHabitacion().getId()
            )).toList();
            
            return ResponseEntity.ok(reservasDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener tus reservas");
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @reservaService.isReservaOwner(#id, authentication.name))")
    public ResponseEntity<?> getReservaById(@PathVariable Long id) {
        try {
            Optional<Reserva> reservaOpt = reservaService.findById(id);
            if (reservaOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Reserva no encontrada");
            }
            return ResponseEntity.ok(reservaOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener la reserva");
        }
    }

    // CREAR RESERVA - SOLO CLIENTES
    @PostMapping("/crear")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> createReserva(@Valid @RequestBody ReservaDTO reservaDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Verificar que el cliente existe
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Usuario no autenticado correctamente");
            }
            
            Optional<Cliente> clienteOpt = clienteService.findByUsuarioId(usuarioOpt.get().getId());
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Cliente no encontrado");
            }
            
            // Asignar el cliente actual a la reserva
            reservaDTO.setClienteId(clienteOpt.get().getId());
            
            // Crear la reserva con validaciones
            Reserva reserva = reservaService.createReservaFromDTO(reservaDTO);
            return ResponseEntity.ok(reserva);
            
        } catch (BusinessException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear la reserva: " + e.getMessage());
        }
    }

    // CANCELAR RESERVA - SOLO EL CLIENTE DUEÑO
    @DeleteMapping("/cancelar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Verificar que la reserva existe y pertenece al cliente
            Optional<Reserva> reservaOpt = reservaService.findById(id);
            if (reservaOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Reserva no encontrada");
            }
            
            Reserva reserva = reservaOpt.get();
            
            // Verificar que es el dueño de la reserva
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Usuario no autenticado");
            }
            
            Optional<Cliente> clienteOpt = clienteService.findByUsuarioId(usuarioOpt.get().getId());
            if (clienteOpt.isEmpty() || !clienteOpt.get().getId().equals(reserva.getCliente().getId())) {
                return ResponseEntity.status(403).body("No tienes permisos para cancelar esta reserva");
            }
            
            // Verificar que la reserva se puede cancelar (estado PENDIENTE)
            if (!"PENDIENTE".equals(reserva.getEstadoReserva())) {
                return ResponseEntity.status(400).body("Solo se pueden cancelar reservas en estado PENDIENTE");
            }
            
            reservaService.deleteById(id);
            return ResponseEntity.ok("Reserva cancelada exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cancelar la reserva");
        }
    }

    // CONFIRMAR RESERVA - SOLO EL CLIENTE DUEÑO
    @PutMapping("/confirmar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Verificar propiedad de la reserva
            Optional<Reserva> reservaOpt = reservaService.findById(id);
            if (reservaOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Reserva no encontrada");
            }
            
            Reserva reserva = reservaOpt.get();
            
            // Verificar que es el dueño
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Usuario no autenticado");
            }
            
            Optional<Cliente> clienteOpt = clienteService.findByUsuarioId(usuarioOpt.get().getId());
            if (clienteOpt.isEmpty() || !clienteOpt.get().getId().equals(reserva.getCliente().getId())) {
                return ResponseEntity.status(403).body("No tienes permisos para confirmar esta reserva");
            }
            
            // Confirmar reserva
            reserva.setEstadoReserva("CONFIRMADA");
            Reserva reservaActualizada = reservaService.save(reserva);
            
            return ResponseEntity.ok("Reserva confirmada exitosamente. Proceda con el pago.");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al confirmar la reserva");
        }
    }

    // ADMIN - CRUD COMPLETO
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaDTO reservaDTO) {
        try {
            Optional<Reserva> reservaOpt = reservaService.updateReservaFromDTO(id, reservaDTO);
            if (reservaOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Reserva no encontrada");
            }
            return ResponseEntity.ok(reservaOpt.get());
        } catch (BusinessException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar la reserva");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        try {
            if (reservaService.existsById(id)) {
                reservaService.deleteById(id);
                return ResponseEntity.ok("Reserva eliminada exitosamente");
            } else {
                return ResponseEntity.status(404).body("Reserva no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar la reserva");
        }
    }
}