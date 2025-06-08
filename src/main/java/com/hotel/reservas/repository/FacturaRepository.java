package com.hotel.reservas.repository;
// src/main/java/com/hotel/repository/FacturaRepository.java

import com.hotel.reservas.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
}
