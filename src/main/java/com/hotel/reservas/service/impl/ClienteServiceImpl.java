package com.hotel.reservas.service.impl;

import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.repository.ClienteRepository;
import com.hotel.reservas.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return clienteRepository.existsById(id);
    }

    @Override
    public Optional<Cliente> findByUsuarioId(Long usuarioId) {
        System.out.println("Buscando cliente por Usuario ID: " + usuarioId);
        Optional<Cliente> cliente = clienteRepository.findByUsuarioId(usuarioId);
        if (cliente.isPresent()) {
            System.out.println("Cliente encontrado: " + cliente.get().getId());
        } else {
            System.out.println("No se encontró cliente con Usuario ID: " + usuarioId);
        }
        return cliente;
    }

    @Override
    public boolean existsByEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsByEmail'");
    }

    @Override
    public boolean tieneReservasActivas(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tieneReservasActivas'");
    }
}
