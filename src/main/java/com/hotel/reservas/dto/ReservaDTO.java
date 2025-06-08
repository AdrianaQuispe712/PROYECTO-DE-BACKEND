package com.hotel.reservas.dto;

import java.time.LocalDate;

public class ReservaDTO {

    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long clienteId;
    private Long habitacionId;

    public ReservaDTO() {
    }

    public ReservaDTO(Long id, LocalDate fechaInicio, LocalDate fechaFin, Long clienteId, Long habitacionId) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.clienteId = clienteId;
        this.habitacionId = habitacionId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
    public Long getClienteId() {
        return clienteId;
    }
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    public Long getHabitacionId() {
        return habitacionId;
    }
    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }
}
