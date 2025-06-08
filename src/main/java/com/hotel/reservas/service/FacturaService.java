package com.hotel.reservas.service;
// src/main/java/com/hotel/service/FacturaService.java


import com.hotel.reservas.dto.FacturaDTO;
import com.hotel.reservas.model.Factura;

public interface FacturaService {
    FacturaDTO generarFactura(Long reservaId, int cantidadPersonas);

    Factura obtenerFacturaPorId(Long id);

}
