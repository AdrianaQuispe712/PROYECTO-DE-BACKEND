package com.hotel.reservas.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaDTO {
    private Long id;
    private Long reservaId;
    private LocalDate fechaEmision;
    private BigDecimal montoTotal;
    private int cantidadPersonas;
    private int diasEstancia;
    private boolean descuentoAplicado;
    private BigDecimal montoFinal;
}
