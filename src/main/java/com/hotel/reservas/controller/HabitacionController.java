package com.hotel.reservas.controller;

import com.hotel.reservas.model.Habitacion;
import com.hotel.reservas.service.HabitacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;

    @GetMapping
    public List<Habitacion> getAllHabitaciones() {
        return habitacionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> getHabitacionById(@PathVariable Long id) {
        return habitacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Habitacion createHabitacion(@Valid @RequestBody Habitacion habitacion) {
        return habitacionService.save(habitacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> updateHabitacion(@PathVariable Long id, @Valid @RequestBody Habitacion habitacionDetails) {
        return habitacionService.findById(id)
                .map(habitacion -> {
                    habitacion.setNumero(habitacionDetails.getNumero());
                    habitacion.setTipo(habitacionDetails.getTipo());
                    habitacion.setCapacidad(habitacionDetails.getCapacidad());
                    habitacion.setPrecio(habitacionDetails.getPrecio());
                    habitacion.setEstado(habitacionDetails.getEstado());
                    Habitacion updated = habitacionService.save(habitacion);
                    return ResponseEntity.ok(updated);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitacion(@PathVariable Long id) {
        if (habitacionService.existsById(id)) {
            habitacionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
