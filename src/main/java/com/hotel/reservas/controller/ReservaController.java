package com.hotel.reservas.controller;

import com.hotel.reservas.dto.ReservaDTO;
import com.hotel.reservas.model.Reserva;
import com.hotel.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
public List<ReservaDTO> getAllReservas() {
    List<Reserva> reservas = reservaService.findAll();
    return reservas.stream().map(reserva -> new ReservaDTO(
        reserva.getId(),
        reserva.getFechaInicio(),
        reserva.getFechaFin(),
        reserva.getCliente().getId(),
        reserva.getHabitacion().getId(),
        reserva.getEmpleado().getId()
    )).toList();
}


    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Long id) {
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reserva> createReserva(@Valid @RequestBody ReservaDTO reservaDTO) {
        Reserva reserva = reservaService.createReservaFromDTO(reservaDTO);
        return ResponseEntity.ok(reserva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaDTO reservaDTO) {
        return reservaService.updateReservaFromDTO(id, reservaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) {
        if (reservaService.existsById(id)) {
            reservaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
