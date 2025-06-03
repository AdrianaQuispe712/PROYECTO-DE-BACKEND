package com.hotel.reservas.service;

import com.hotel.reservas.model.Habitacion;

import java.util.List;
import java.util.Optional;

public interface HabitacionService {
    List<Habitacion> findAll();
    Optional<Habitacion> findById(Long id);
    Habitacion save(Habitacion habitacion);
    void deleteById(Long id);
    boolean existsById(Long id);
}
