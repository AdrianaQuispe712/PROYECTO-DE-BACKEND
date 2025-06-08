package com.hotel.reservas.service.impl;

import com.hotel.reservas.dto.ReservaDTO;
import com.hotel.reservas.model.Cliente;
import com.hotel.reservas.model.Habitacion;
import com.hotel.reservas.model.Reserva;
import com.hotel.reservas.repository.ClienteRepository;
import com.hotel.reservas.repository.HabitacionRepository;
import com.hotel.reservas.repository.ReservaRepository;
import com.hotel.reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;


    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    @Override
    public Reserva createReservaFromDTO(ReservaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + dto.getClienteId()));
        Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada con id: " + dto.getHabitacionId()));

        Reserva reserva = new Reserva();
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion);

        return reservaRepository.save(reserva);
    }

    @Override
    public Optional<Reserva> updateReservaFromDTO(Long id, ReservaDTO dto) {
        return reservaRepository.findById(id).map(reserva -> {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + dto.getClienteId()));
            Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada con id: " + dto.getHabitacionId()));
    
            reserva.setFechaInicio(dto.getFechaInicio());
            reserva.setFechaFin(dto.getFechaFin());
            reserva.setCliente(cliente);
            reserva.setHabitacion(habitacion);

            return reservaRepository.save(reserva);
        });
    }

    @Override
    public boolean existsById(Long id) {
        return reservaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

   @Override
    public List<Reserva> findByClienteId(Long id) {
    return reservaRepository.findByClienteId(id);
    }


   @Override
public Reserva save(Reserva reserva) {
    return reservaRepository.save(reserva);
}

   @Override
   public boolean isReservaOwner(Long idReserva, String username) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isReservaOwner'");
   }

}
