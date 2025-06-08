package com.hotel.reservas.login.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.reservas.login.model.Usuario;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);

}
