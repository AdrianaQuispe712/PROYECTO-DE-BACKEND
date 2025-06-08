package com.hotel.reservas.service.impl;

import com.hotel.reservas.model.Habitacion;
import com.hotel.reservas.repository.HabitacionRepository;
import com.hotel.reservas.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Override
    public List<Habitacion> findAll() {
        return habitacionRepository.findAll();
    }

    @Override
    public Optional<Habitacion> findById(Long id) {
        return habitacionRepository.findById(id);
    }

    @Override
    public Habitacion save(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Override
    public void deleteById(Long id) {
        habitacionRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return habitacionRepository.existsById(id);
    }

    @Override
    public boolean esEstadoValido(String estado) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'esEstadoValido'");
    }

    @Override
    public boolean tieneReservasActivas(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tieneReservasActivas'");
    }

    @Override
    public boolean esTipoValido(String tipo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'esTipoValido'");
    }

    @Override
    public boolean existsByNumero(String numero) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsByNumero'");
    }

    @Override
    public List<Habitacion> findByTipo(String upperCase) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTipo'");
    }

    @Override
    public List<Habitacion> findHabitacionesDisponibles(LocalDate fechaInicio, LocalDate fechaFin, Integer personas) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findHabitacionesDisponibles'");
    }
}
