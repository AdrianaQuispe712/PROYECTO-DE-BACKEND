package com.hotel.reservas.service;

import com.hotel.reservas.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);
    Cliente save(Cliente cliente);
    void deleteById(Long id);
    boolean existsById(Long id);
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    boolean existsByEmail(String email);
    boolean tieneReservasActivas(Long id);
}
