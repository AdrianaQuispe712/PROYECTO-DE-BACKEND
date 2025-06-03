package com.hotel.reservas.repository;

import com.hotel.reservas.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Métodos personalizados opcionales
}
