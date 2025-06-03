package com.hotel.reservas.service;

import com.hotel.reservas.model.Pago;

import java.util.List;
import java.util.Optional;

public interface PagoService {
    List<Pago> findAll();
    Optional<Pago> findById(Long id);
    Pago save(Pago pago);
    void deleteById(Long id);
    boolean existsById(Long id);
}
