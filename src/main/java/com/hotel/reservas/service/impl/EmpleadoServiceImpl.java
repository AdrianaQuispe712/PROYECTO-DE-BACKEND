package com.hotel.reservas.service.impl;

import com.hotel.reservas.dto.EmpleadoDTO;
import com.hotel.reservas.model.Empleado;
import com.hotel.reservas.repository.EmpleadoRepository;
import com.hotel.reservas.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Override
    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) {
        Empleado empleado = Empleado.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .ci(dto.getCi())
                .puesto(dto.getPuesto())
                .build();
        Empleado guardado = empleadoRepository.save(empleado);
        return mapToDTO(guardado);
    }

    @Override
    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setCi(dto.getCi());
        empleado.setPuesto(dto.getPuesto());
        return mapToDTO(empleadoRepository.save(empleado));
    }

    @Override
    public void eliminarEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }

    @Override
    public List<EmpleadoDTO> listarEmpleados() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmpleadoDTO obtenerEmpleadoPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        return mapToDTO(empleado);
    }

    private EmpleadoDTO mapToDTO(Empleado empleado) {
        return EmpleadoDTO.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .apellido(empleado.getApellido())
                .ci(empleado.getCi())
                .puesto(empleado.getPuesto())
                .build();
    }
}
