package com.hotel.reservas.service;

import com.hotel.reservas.dto.ReservaDTO;
import com.hotel.reservas.model.Reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaService {

    List<Reserva> findAll();
    Optional<Reserva> findById(Long id);
    Reserva createReservaFromDTO(ReservaDTO dto);
    Optional<Reserva> updateReservaFromDTO(Long id, ReservaDTO dto);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<Reserva> findByClienteId(Long id);
    Reserva save(Reserva reserva);
    boolean isReservaOwner(Long idReserva, String username);

}
