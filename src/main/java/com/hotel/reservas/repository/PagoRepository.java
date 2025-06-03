package com.hotel.reservas.repository;

import com.hotel.reservas.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    // Métodos personalizados opcionales
}
