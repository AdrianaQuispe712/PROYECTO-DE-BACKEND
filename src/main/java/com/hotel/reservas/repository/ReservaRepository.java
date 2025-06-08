package com.hotel.reservas.repository;

import com.hotel.reservas.model.Reserva;
import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Métodos personalizados opcionales
    long countByClienteId(Long clienteId);
    List<Reserva> findByClienteId(Long clienteId);



}
