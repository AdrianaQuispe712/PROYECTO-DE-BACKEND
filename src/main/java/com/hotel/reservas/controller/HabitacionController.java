package com.hotel.reservas.controller;

import com.hotel.reservas.model.Habitacion;
import com.hotel.reservas.service.HabitacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    // PÚBLICO - Ver todas las habitaciones (para que clientes vean opciones)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionService.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lista de habitaciones obtenida correctamente");
            response.put("data", habitaciones);
            response.put("total", habitaciones.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener habitaciones: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // PÚBLICO - Ver habitación específica
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHabitacionById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            return habitacionService.findById(id)
                .map(habitacion -> {
                    response.put("success", true);
                    response.put("message", "Habitación encontrada");
                    response.put("data", habitacion);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Habitación no encontrada con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar habitación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // PÚBLICO - Ver habitaciones disponibles en fechas específicas
    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> getHabitacionesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer personas) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar fechas
            if (fechaInicio.isAfter(fechaFin)) {
                response.put("success", false);
                response.put("message", "La fecha de inicio no puede ser posterior a la fecha de fin");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (fechaInicio.isBefore(LocalDate.now())) {
                response.put("success", false);
                response.put("message", "La fecha de inicio no puede ser anterior al día actual");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            List<Habitacion> habitacionesDisponibles = habitacionService.findHabitacionesDisponibles(fechaInicio, fechaFin, personas);
            
            response.put("success", true);
            response.put("message", "Habitaciones disponibles encontradas");
            response.put("data", habitacionesDisponibles);
            response.put("total", habitacionesDisponibles.size());
            response.put("fechaInicio", fechaInicio);
            response.put("fechaFin", fechaFin);
            response.put("personas", personas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar habitaciones disponibles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // PÚBLICO - Ver habitaciones por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Map<String, Object>> getHabitacionesPorTipo(@PathVariable String tipo) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Habitacion> habitaciones = habitacionService.findByTipo(tipo.toUpperCase());
            response.put("success", true);
            response.put("message", "Habitaciones del tipo " + tipo + " encontradas");
            response.put("data", habitaciones);
            response.put("total", habitaciones.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar habitaciones por tipo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // SOLO ADMIN - Crear nueva habitación
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createHabitacion(@Valid @RequestBody Habitacion habitacion) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar que no existe habitación con el mismo número
            if (habitacionService.existsByNumero(habitacion.getNumero())) {
                response.put("success", false);
                response.put("message", "Ya existe una habitación con el número: " + habitacion.getNumero());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            // Validar tipo de habitación
            if (!habitacionService.esTipoValido(habitacion.getTipo())) {
                response.put("success", false);
                response.put("message", "Tipo de habitación no válido. Tipos permitidos: INDIVIDUAL, DOBLE, SUITE");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Establecer estado por defecto
            if (habitacion.getEstado() == null || habitacion.getEstado().isEmpty()) {
                habitacion.setEstado("DISPONIBLE");
            }
            
            Habitacion nuevaHabitacion = habitacionService.save(habitacion);
            response.put("success", true);
            response.put("message", "Habitación creada exitosamente");
            response.put("data", nuevaHabitacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear habitación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // SOLO ADMIN - Actualizar habitación
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateHabitacion(@PathVariable Long id, @Valid @RequestBody Habitacion habitacionDetails) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            return habitacionService.findById(id)
                .map(habitacion -> {
                    // Validar número único (solo si se cambió)
                    if (!habitacion.getNumero().equals(habitacionDetails.getNumero()) && 
                        habitacionService.existsByNumero(habitacionDetails.getNumero())) {
                        response.put("success", false);
                        response.put("message", "Ya existe otra habitación con el número: " + habitacionDetails.getNumero());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    }
                    
                    // Validar tipo
                    if (!habitacionService.esTipoValido(habitacionDetails.getTipo())) {
                        response.put("success", false);
                        response.put("message", "Tipo de habitación no válido. Tipos permitidos: INDIVIDUAL, DOBLE, SUITE");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                    
                    // Actualizar campos
                    habitacion.setNumero(habitacionDetails.getNumero());
                    habitacion.setTipo(habitacionDetails.getTipo());
                    habitacion.setCapacidad(habitacionDetails.getCapacidad());
                    habitacion.setPrecio(habitacionDetails.getPrecio());
                    habitacion.setEstado(habitacionDetails.getEstado());
                    
                    Habitacion habitacionActualizada = habitacionService.save(habitacion);
                    response.put("success", true);
                    response.put("message", "Habitación actualizada exitosamente");
                    response.put("data", habitacionActualizada);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Habitación no encontrada con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar habitación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // SOLO ADMIN - Eliminar habitación
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteHabitacion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!habitacionService.existsById(id)) {
                response.put("success", false);
                response.put("message", "Habitación no encontrada con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Verificar si tiene reservas activas
            if (habitacionService.tieneReservasActivas(id)) {
                response.put("success", false);
                response.put("message", "No se puede eliminar la habitación porque tiene reservas activas");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            habitacionService.deleteById(id);
            response.put("success", true);
            response.put("message", "Habitación eliminada exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar habitación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // SOLO ADMIN - Cambiar estado de habitación
    @PutMapping("/admin/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cambiarEstadoHabitacion(@PathVariable Long id, @RequestParam String estado) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (!habitacionService.esEstadoValido(estado)) {
                response.put("success", false);
                response.put("message", "Estado no válido. Estados permitidos: DISPONIBLE, OCUPADA, MANTENIMIENTO");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            return habitacionService.findById(id)
                .map(habitacion -> {
                    habitacion.setEstado(estado.toUpperCase());
                    Habitacion habitacionActualizada = habitacionService.save(habitacion);
                    response.put("success", true);
                    response.put("message", "Estado de habitación actualizado exitosamente");
                    response.put("data", habitacionActualizada);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Habitación no encontrada con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar estado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}