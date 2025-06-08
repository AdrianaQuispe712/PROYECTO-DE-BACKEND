package com.hotel.reservas.service;

import com.hotel.reservas.model.Habitacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitacionService {
    List<Habitacion> findAll();
    Optional<Habitacion> findById(Long id);
    Habitacion save(Habitacion habitacion);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean esEstadoValido(String estado);
    boolean tieneReservasActivas(Long id);
    boolean esTipoValido(String tipo);
    boolean existsByNumero(String numero);
    List<Habitacion> findByTipo(String upperCase);
    List<Habitacion> findHabitacionesDisponibles(LocalDate fechaInicio, LocalDate fechaFin, Integer personas);
}
