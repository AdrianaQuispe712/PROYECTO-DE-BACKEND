package com.hotel.reservas.validation;

import com.hotel.reservas.dto.ReservaDTO;

import java.time.LocalDate;

public class ReservaValidator {

    public static void validarFechas(ReservaDTO reserva) {
        if (reserva.getFechaInicio().isAfter(reserva.getFechaFin())) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
            throw new BusinessException("La fecha de inicio no puede ser en el pasado.");
        }
    }
}
