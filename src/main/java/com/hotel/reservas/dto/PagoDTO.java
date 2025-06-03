package com.hotel.reservas.dto;

import java.math.BigDecimal;

public class PagoDTO {

    private Long id;
    private BigDecimal monto;
    private String metodo;
    private String estado;
    private Long reservaId;

    public PagoDTO() {
    }

    public PagoDTO(Long id, BigDecimal monto, String metodo, String estado, Long reservaId) {
        this.id = id;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.reservaId = reservaId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    public String getMetodo() {
        return metodo;
    }
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Long getReservaId() {
        return reservaId;
    }
    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }
}
