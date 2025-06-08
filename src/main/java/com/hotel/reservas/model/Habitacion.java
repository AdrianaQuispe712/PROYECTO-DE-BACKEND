package com.hotel.reservas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "habitacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Habitacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 20, message = "El número no puede tener más de 20 caracteres")
    @Column(nullable = false, length = 20)
    private String numero;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 50, message = "El tipo no puede tener más de 50 caracteres")
    @Column(nullable = false, length = 50)
    private String tipo;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer capacidad;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 50, message = "El estado no puede tener más de 50 caracteres")
    @Column(nullable = false, length = 50)
    private String estado;

    @Version
    private Integer version;
    
    @Builder.Default
@Column(nullable = false)
private Boolean disponible = true;
}
