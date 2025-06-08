package com.hotel.reservas.service.impl;
import com.hotel.reservas.dto.FacturaDTO;
import com.hotel.reservas.model.*;
import com.hotel.reservas.repository.*;
import com.hotel.reservas.service.FacturaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final ReservaRepository reservaRepository;
    private final FacturaRepository facturaRepository;

    @Override
    @Transactional
    public FacturaDTO generarFactura(Long reservaId, int cantidadPersonas) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Habitacion habitacion = reserva.getHabitacion();

        long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        if (dias == 0) dias = 1; // mínimo 1 día de cobro

        BigDecimal montoTotal = habitacion.getPrecio().multiply(BigDecimal.valueOf(dias));

        boolean descuento = aplicarDescuento(reserva.getCliente().getId());
        BigDecimal montoFinal = descuento ? montoTotal.multiply(BigDecimal.valueOf(0.90)) : montoTotal;

        Factura factura = Factura.builder()
                .reserva(reserva)
                .fechaEmision(LocalDate.now())
                .montoTotal(montoTotal)
                .cantidadPersonas(cantidadPersonas)
                .diasEstancia((int) dias)
                .descuentoAplicado(descuento)
                .montoFinal(montoFinal)
                .build();

        facturaRepository.save(factura);

        return FacturaDTO.builder()
                .id(factura.getId())
                .reservaId(reserva.getId())
                .fechaEmision(factura.getFechaEmision())
                .montoTotal(factura.getMontoTotal())
                .cantidadPersonas(factura.getCantidadPersonas())
                .diasEstancia(factura.getDiasEstancia())
                .descuentoAplicado(factura.isDescuentoAplicado())
                .montoFinal(factura.getMontoFinal())
                .build();
    }

    private boolean aplicarDescuento(Long clienteId) {
        long cantidadReservas = reservaRepository.countByClienteId(clienteId);
        return cantidadReservas >= 10;
    }

    @Override
    public Factura obtenerFacturaPorId(Long id) {
        return facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    
}
