package com.hotel.reservas.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Reserva reserva;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false)
    private BigDecimal montoTotal;

    @Column(nullable = false)
    private int cantidadPersonas;

    @Column(nullable = false)
    private int diasEstancia;

    @Column(nullable = false)
    private boolean descuentoAplicado;

    @Column(nullable = false)
    private BigDecimal montoFinal;
}
