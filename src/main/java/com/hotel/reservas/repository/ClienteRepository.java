package com.hotel.reservas.repository;

import com.hotel.reservas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Método para buscar un cliente por su usuario
    Optional<Cliente> findByUsuarioId(Long usuarioId);
}
