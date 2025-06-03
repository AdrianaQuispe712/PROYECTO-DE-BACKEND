package com.hotel.reservas.service;

import com.hotel.reservas.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoService {
    List<Empleado> findAll();
    Optional<Empleado> findById(Long id);
    Empleado save(Empleado empleado);
    void deleteById(Long id);
    boolean existsById(Long id);
}
