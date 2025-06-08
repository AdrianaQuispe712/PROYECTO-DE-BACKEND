package com.hotel.reservas.service;



import com.hotel.reservas.dto.EmpleadoDTO;

import java.util.List;

public interface EmpleadoService {
    EmpleadoDTO crearEmpleado(EmpleadoDTO dto);
    EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto);
    void eliminarEmpleado(Long id);
    List<EmpleadoDTO> listarEmpleados();
    EmpleadoDTO obtenerEmpleadoPorId(Long id);
}

